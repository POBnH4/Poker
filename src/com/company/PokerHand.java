package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

class PokerHand extends PokerPlayer {

    private String cards;

    private final char CARD_SUIT_SPADES = 'S', CARD_SUIT_HEARTS = 'H', CARD_SUIT_DIAMONDS = 'D', CARD_SUIT_CLUBS = 'C';

    private final char CARD_TWO = '2', CARD_THREE = '3', CARD_FOUR = '4', CARD_FIVE = '5';
    private final char CARD_SIX = '6', CARD_SEVEN = '7', CARD_EIGHT = '8', CARD_NINE = '9';
    private final char CARD_TEN = 'T', CARD_JACK = 'J', CARD_QUEEN = 'Q', CARD_KING = 'K', CARD_ACE = 'A';

    private final int CARD_TWO_VALUE = 2, CARD_THREE_VALUE = 3, CARD_FOUR_VALUE = 4, CARD_FIVE_VALUE = 5;
    private final int CARD_SIX_VALUE = 6, CARD_SEVEN_VALUE = 7, CARD_EIGHT_VALUE = 8, CARD_NINE_VALUE = 9;
    private final int CARD_TEN_VALUE = 10, CARD_JACK_VALUE = 11, CARD_QUEEN_VALUE = 12, CARD_KING_VALUE = 13;
    private final int CARD_ACE_VALUE = 14;

    private final int WIN_GAME = 1, LOSS_GAME = 2, TIE_GAME = 3;
    private final int CARD_TYPE = 0, CARD_SUIT = 1;

    // field below preserve the types of the cards in case of a tie(when someone has the same hand values);
    private char PAIR_CARD_TYPE = ' '; // the type of card that is in a pair;
    private char PAIR_TYPE_SPARE_CARD = ' '; // the type card with the highest value without counting the pair;
    private char TWO_PAIRS_FIRST_PAIR_TYPE = ' '; //the type of card from the first pair;
    private char TWO_PAIRS_SECOND_PAIR_TYPE = ' '; //the type of card from the second pair;
    private char THREE_OF_A_KIND_TYPE = ' '; //the type of card that appears three times in three of a kind hand;
    private char THREE_OF_A_KIND_TYPE_SPARE_CARD = ' '; // the type of card that has the highest without counting the cards that are in 'Three of a kind';
    private char FULL_HOUSE_THREE_OF_A_KIND_TYPE = ' '; //given cards that have a Full house, the value of three of a kind card
    private char FULL_HOUSE_PAIR_TYPE = ' ';//given cards that have a Full house,the type of card with highest value in the pair;
    private char FOUR_OF_A_KIND_TYPE = ' '; // the type of card that appears four times in three of a kind hand;

    private final int NUMBER_OF_CARDS_IN_A_PAIR = 2, THREE_OF_A_KIND_CARDS = 3;
    private final int FIRST_CARD = 0;

    private PokerHand comparePlayer;

    private final char EMPTY_SPACE_DELIMITER = ' ';

    private final HashMap<Character, Integer> cardValues;

    PokerHand(String name, String cards) {
        super(name);
        this.cards = cards;
        this.cardValues = new HashMap<>();
        setUpCardValues(this.cardValues);
    }


    /**
     * Method that compares who wins,losses, or whether there is a tie;
     *
     * @param comparePlayer is the other player whose cards are going to be compared;
     **/
    int compareWith(PokerHand comparePlayer) {

        this.comparePlayer = new PokerHand(comparePlayer.getName(), comparePlayer.getCards());

        String[] currentPlayerCards = convertToArray(this.cards);
        String[] comparePlayerCards = convertToArray(this.comparePlayer.getCards());

        sortCardsByValue(currentPlayerCards);
        sortCardsByValue(comparePlayerCards);

        //sort the cards in the other player's hand since sorting the
        // comparePlayerCards above is sorting just an instance of the actual value;
        //NOTE: NOT sorting them in their actual class could result in bugs;
        this.comparePlayer.sortCardsByValue(convertToArray(this.comparePlayer.getCards()));

        Hand currentPlayerHand = checkHand(currentPlayerCards);
        Hand comparePlayerHand = this.comparePlayer.checkHand(comparePlayerCards);

        //if both players do not have any
        if (currentPlayerHand == Hand.HIGH_CARD && comparePlayerHand == Hand.HIGH_CARD) {
            int currentPlayerHighestValueCard = getHighestValueCard(currentPlayerCards); //check highest card(Simple value of the card. Lowest: 2 - Highest: Ace	);
            int comparePlayerHighestValueCard = getHighestValueCard(comparePlayerCards);

            if (currentPlayerHighestValueCard > comparePlayerHighestValueCard) return WIN_GAME;
            else if (currentPlayerHighestValueCard < comparePlayerHighestValueCard) return LOSS_GAME;
            else return TIE_GAME;
        }

        return checkWinner(currentPlayerHand, comparePlayerHand, currentPlayerCards, comparePlayerCards);
    }

    /**
     * @param hand current cards in a player's hand;
     **/
    private Hand checkHand(String[] hand) {
        //methods below check to see what the other person has in their hands;
        boolean containsPair = isPair(hand);               //check for pairs(Two cards with the same value	);
        boolean containsTwoPairs = areTwoPairs(hand);           //check for two pairs(Two times two cards with the same value	);
        boolean containsThreeOfAKind = areThreeOfAKind(hand);       //check for three of a kind(Three cards with the same value	);
        boolean containsFourOfAKind = areFourOfAKind(hand);        //check for four of a kind (Four cards of the same value	);
        boolean containsStraight = isStraight(hand);           //check for straight(Sequence of 5 cards in increasing value (Ace can precede 2 and follow up King));
        boolean containsFullHouse = isFullHouse(hand);          //check for full house(Combination of three of a kind and a pair	);
        boolean containsFlush = isFlush(hand);              //check for flush(5 cards of the same suit)
        boolean containsStraightFlush = isStraightFlush(hand);      //check for straight flush(Straight of the same suit);
        boolean containsRoyalFlush = isRoyalFlush(hand);         //check for royal flush (Straight flush from Ten to Ace);

        return getStrongestValueOfCards(containsPair, containsTwoPairs,
                containsThreeOfAKind, containsFourOfAKind, containsStraight,
                containsFullHouse, containsFlush, containsStraightFlush, containsRoyalFlush);
    }

    /**
     * @param containsPair          boolean saying whether a pair was found in the given hand;
     * @param containsTwoPairs      boolean saying whether two pairs were found in the given hand;
     * @param containsThreeOfAKind  boolean saying whether three of a kind were found in the given hand;
     * @param containsFourOfAKind   boolean saying whether four of a kind were found in the given hand;
     * @param containsStraight      boolean saying whether straight was found in the given hand;
     * @param containsFullHouse     boolean saying whether a full house was found in the given hand;
     * @param containsFlush         boolean saying whether flush was found in the given hand;
     * @param containsStraightFlush boolean saying whether straight flush was found in the given hand;
     * @param containsRoyalFlush    boolean saying whether royal flush pair was found in the given hand;
     **/
    private Hand getStrongestValueOfCards(boolean containsPair, boolean containsTwoPairs,
                                          boolean containsThreeOfAKind, boolean containsFourOfAKind, boolean containsStraight,
                                          boolean containsFullHouse, boolean containsFlush, boolean containsStraightFlush,
                                          boolean containsRoyalFlush) {

        if (containsRoyalFlush) return Hand.ROYAL_FLUSH;
        if (containsStraightFlush) return Hand.STRAIGHT_FLUSH;
        if (containsFourOfAKind) return Hand.FOUR_OF_A_KIND;
        if (containsFullHouse) return Hand.FULL_HOUSE;
        if (containsFlush) return Hand.FLUSH;
        if (containsStraight) return Hand.STRAIGHT;
        if (containsThreeOfAKind) return Hand.THREE_OF_A_KIND;
        if (containsTwoPairs) return Hand.TWO_PAIRS;
        if (containsPair) return Hand.PAIR;

        return Hand.HIGH_CARD;
    }

    /**
     * @param currentPlayer      strongest value the currentPlayer has;
     * @param comparePlayer      strongest value the comparePlayer has;
     * @param currentPlayerCards sorted cards of the currentPlayer;
     * @param comparePlayerCards sorted cards of the comparePlayer;
     **/
    private int checkWinner(Hand currentPlayer, Hand comparePlayer, String[] currentPlayerCards, String[] comparePlayerCards) {
        // Royal Flush is the strongest Poker hand and appears only once so there is no tie;
        //if statements below are ordered in terms of how strong they are;
        if (currentPlayer == Hand.ROYAL_FLUSH) return WIN_GAME;
        if (comparePlayer == Hand.ROYAL_FLUSH) return LOSS_GAME;

        if (currentPlayer == Hand.STRAIGHT_FLUSH && comparePlayer == Hand.STRAIGHT_FLUSH)
            return straightFlushTieChecker(currentPlayerCards, comparePlayerCards);
        if (currentPlayer == Hand.STRAIGHT_FLUSH) return WIN_GAME;
        if (comparePlayer == Hand.STRAIGHT_FLUSH) return LOSS_GAME;

        if (currentPlayer == Hand.FOUR_OF_A_KIND && comparePlayer == Hand.FOUR_OF_A_KIND)
            return fourOfAKindTieChecker(this.comparePlayer);
        if (currentPlayer == Hand.FOUR_OF_A_KIND) return WIN_GAME;
        if (comparePlayer == Hand.FOUR_OF_A_KIND) return LOSS_GAME;

        if (currentPlayer == Hand.FULL_HOUSE && comparePlayer == Hand.FULL_HOUSE)
            return fullHouseTieChecker(this.comparePlayer);
        if (currentPlayer == Hand.FULL_HOUSE) return WIN_GAME;
        if (comparePlayer == Hand.FULL_HOUSE) return LOSS_GAME;

        if (currentPlayer == Hand.FLUSH && comparePlayer == Hand.FLUSH)
            return flushTieChecker(currentPlayerCards, comparePlayerCards);
        if (currentPlayer == Hand.FLUSH) return WIN_GAME;
        if (comparePlayer == Hand.FLUSH) return LOSS_GAME;

        if (currentPlayer == Hand.STRAIGHT && comparePlayer == Hand.STRAIGHT)
            return straightTieChecker(currentPlayerCards, comparePlayerCards);
        if (currentPlayer == Hand.STRAIGHT) return WIN_GAME;
        if (comparePlayer == Hand.STRAIGHT) return LOSS_GAME;

        if (currentPlayer == Hand.THREE_OF_A_KIND && comparePlayer == Hand.THREE_OF_A_KIND)
            return threeOfAKindTieChecker(this.comparePlayer);
        if (currentPlayer == Hand.THREE_OF_A_KIND) return WIN_GAME;
        if (comparePlayer == Hand.THREE_OF_A_KIND) return LOSS_GAME;

        if (currentPlayer == Hand.TWO_PAIRS && comparePlayer == Hand.TWO_PAIRS)
            return twoPairsTieChecker(this.comparePlayer);
        if (currentPlayer == Hand.TWO_PAIRS) return WIN_GAME;
        if (comparePlayer == Hand.TWO_PAIRS) return LOSS_GAME;

        if (currentPlayer == Hand.PAIR && comparePlayer == Hand.PAIR)
            return pairTieChecker(this.comparePlayer);
        if (currentPlayer == Hand.PAIR) return WIN_GAME;
        if (comparePlayer == Hand.PAIR) return LOSS_GAME;

        return WIN_GAME;
    }

    /**
     * @param currentPlayerCards sorted cards of the currentPlayer;
     * @param comparePlayerCards sorted cards of the comparePlayer;
     *                           The method checks
     **/
    private int straightFlushTieChecker(String[] currentPlayerCards, String[] comparePlayerCards) {
        //Since the cards are sorted with the highest value being last in the array,
        //check which last value is higher;
        //NOTE: since the straight flush has cards
        //only of one suit, this means that there is no TIE;

        //check whether the hand uses 'Ace' as a value that precedes 'Two';
        final int LAST_CARD = currentPlayerCards.length - 1;
        int CHECK_CARD_PLAYER_ONE = currentPlayerCards.length - 1;
        int CHECK_CARD_PLAYER_TWO = currentPlayerCards.length - 1;

        //first player check;
        if (currentPlayerCards[FIRST_CARD].charAt(CARD_TYPE) == CARD_TWO
                && currentPlayerCards[LAST_CARD].charAt(CARD_TYPE) == CARD_ACE) {
            CHECK_CARD_PLAYER_ONE--; // since the cards are sorted low to high, the last card is 'Ace',
            // however in this case 'Ace' is used as value 1 and it precedes 'Two'
            // which means that the higher card in this case will be the second last card;
        }

        //second player check;
        if (comparePlayerCards[FIRST_CARD].charAt(CARD_TYPE) == CARD_TWO
                && comparePlayerCards[LAST_CARD].charAt(CARD_TYPE) == CARD_ACE) {
            CHECK_CARD_PLAYER_TWO--; // since the cards are sorted low to high, the last card is 'Ace',
            // however in this case 'Ace' is used as value 1 and it precedes 'Two'
            // which means that the higher card in this case will be the second last card;
        }

        int currentPlayerLastCardInHand = getCardValue(currentPlayerCards[CHECK_CARD_PLAYER_ONE].charAt(CARD_TYPE));
        int comparePlayerLastCardInHand = getCardValue(comparePlayerCards[CHECK_CARD_PLAYER_TWO].charAt(CARD_TYPE));

        if (currentPlayerLastCardInHand > comparePlayerLastCardInHand) return WIN_GAME;

        return LOSS_GAME;
    }

    /**
     * @param comparePlayerCards sorted cards of the comparePlayer;
     *                           The method checks whose 'four of a kind' has higher value;
     **/
    private int fourOfAKindTieChecker(PokerHand comparePlayerCards) {

        int currentPlayerFourOfAKind = getCardValue(FOUR_OF_A_KIND_TYPE);
        int comparePlayerFourOfAKind = getCardValue(comparePlayerCards.getFOUR_OF_A_KIND_TYPE());

        if (currentPlayerFourOfAKind > comparePlayerFourOfAKind) return WIN_GAME;
        if (currentPlayerFourOfAKind < comparePlayerFourOfAKind) return LOSS_GAME;

        return TIE_GAME;
    }

    /**
     * @param comparePlayerCards sorted cards of the comparePlayer;
     *                           The method checks whose 'three of a kind' has higher value;
     **/
    private int threeOfAKindTieChecker(PokerHand comparePlayerCards) {

        int currentPlayerHandThreeOfAKind = getCardValue(THREE_OF_A_KIND_TYPE);
        int comparePlayerHandThreeOfAKind = getCardValue(comparePlayerCards.getTHREE_OF_A_KIND_TYPE());

        int currentPlayerHandThreeOfAKindSpareCard = getCardValue(THREE_OF_A_KIND_TYPE_SPARE_CARD);
        int comparePlayerHandThreeOfAKindSpareCard = getCardValue(comparePlayerCards.getTHREE_OF_A_KIND_TYPE_SPARE_CARD());

        if (currentPlayerHandThreeOfAKind > comparePlayerHandThreeOfAKind) return WIN_GAME;
        if (currentPlayerHandThreeOfAKind < comparePlayerHandThreeOfAKind) return WIN_GAME;

        if (currentPlayerHandThreeOfAKindSpareCard > comparePlayerHandThreeOfAKindSpareCard) return WIN_GAME;
        if (currentPlayerHandThreeOfAKindSpareCard < comparePlayerHandThreeOfAKindSpareCard) return LOSS_GAME;

        return TIE_GAME;
    }

    /**
     * @param comparePlayerCards sorted cards of the comparePlayer;
     *                           The method checks whose two pairs have higher value;
     **/
    private int twoPairsTieChecker(PokerHand comparePlayerCards) {

        int currentPlayerPairOne = getHigherValue(getCardValue(TWO_PAIRS_FIRST_PAIR_TYPE), getCardValue(TWO_PAIRS_SECOND_PAIR_TYPE));
        int currentPlayerPairTwo = getLowerValue(getCardValue(TWO_PAIRS_FIRST_PAIR_TYPE), getCardValue(TWO_PAIRS_SECOND_PAIR_TYPE));

        int comparePlayerPairOne = comparePlayerCards.getHigherValue(
                getCardValue(comparePlayerCards.getTWO_PAIRS_FIRST_PAIR_TYPE()),
                getCardValue(comparePlayerCards.getTWO_PAIRS_SECOND_PAIR_TYPE()));

        int comparePlayerPairTwo = comparePlayerCards.getLowerValue(
                getCardValue(comparePlayerCards.getTWO_PAIRS_FIRST_PAIR_TYPE()),
                getCardValue(comparePlayerCards.getTWO_PAIRS_SECOND_PAIR_TYPE()));

        if (currentPlayerPairOne > comparePlayerPairOne) return WIN_GAME;
        if (currentPlayerPairOne < comparePlayerPairOne) return LOSS_GAME;

        if (currentPlayerPairTwo > comparePlayerPairTwo) return WIN_GAME;
        if (currentPlayerPairTwo < comparePlayerPairTwo) return LOSS_GAME;

        return TIE_GAME;
    }

    /**
     * @param comparePlayerCards sorted cards of the comparePlayer;
     *                           The method checks whose pair has higher value;
     **/
    private int pairTieChecker(PokerHand comparePlayerCards) {

        int currentPlayerPair = getCardValue(PAIR_CARD_TYPE);
        int comparePlayerPair = getCardValue(comparePlayerCards.getPAIR_CARD_TYPE());
        int currentPlayerHighestSpareCard = getCardValue(PAIR_TYPE_SPARE_CARD);
        int comparePlayerHighestSpareCard = getCardValue(comparePlayerCards.getPAIR_TYPE_SPARE_CARD());

        if (currentPlayerPair > comparePlayerPair) return WIN_GAME;
        if (currentPlayerPair < comparePlayerPair) return WIN_GAME;

        if (currentPlayerHighestSpareCard > comparePlayerHighestSpareCard) return WIN_GAME;
        if (currentPlayerHighestSpareCard < comparePlayerHighestSpareCard) return LOSS_GAME;

        return TIE_GAME;
    }

    /**
     * @param currentPlayerCards sorted cards of the currentPlayer;
     * @param comparePlayerCards sorted cards of the comparePlayer;
     *                           The method checks whose flush has higher value;
     **/
    private int flushTieChecker(String[] currentPlayerCards, String[] comparePlayerCards) {

        //Since the cards are sorted with the highest value being last in the array,
        //check which last value is higher;
        //NOTE: since the flush has cards
        //only of one suit, this means that there is no TIE;
        int currentPlayerHand = getCardValue(currentPlayerCards[currentPlayerCards.length - 1].charAt(CARD_TYPE));
        int comparePlayerHand = getCardValue(comparePlayerCards[comparePlayerCards.length - 1].charAt(CARD_TYPE));

        if (currentPlayerHand > comparePlayerHand) return WIN_GAME;

        return LOSS_GAME;
    }

    /**
     * @param comparePlayerCards sorted cards of the comparePlayer;
     *                           The method checks whose full house has higher value;
     **/
    private int fullHouseTieChecker(PokerHand comparePlayerCards) {

        int currentPlayerThreeOfAKind = getCardValue(FULL_HOUSE_THREE_OF_A_KIND_TYPE);
        int comparePlayerThreeOfAKind = getCardValue(comparePlayerCards.getFULL_HOUSE_THREE_OF_A_KIND_TYPE());

        int currentPlayerPair = getCardValue(FULL_HOUSE_PAIR_TYPE);
        int comparePlayerPair = getCardValue(comparePlayerCards.getFULL_HOUSE_PAIR_TYPE());

        if (currentPlayerThreeOfAKind > comparePlayerThreeOfAKind) return WIN_GAME;
        if (currentPlayerThreeOfAKind < comparePlayerThreeOfAKind) return WIN_GAME;

        if (currentPlayerPair > comparePlayerPair) return WIN_GAME;
        if (currentPlayerPair < comparePlayerPair) return LOSS_GAME;

        return TIE_GAME;
    }

    /**
     * @param currentPlayerCards sorted cards of the currentPlayer;
     * @param comparePlayerCards sorted cards of the comparePlayer;
     *                           The method checks straight has higher value;
     **/
    private int straightTieChecker(String[] currentPlayerCards, String[] comparePlayerCards) {

        //check whether the hand uses 'Ace' as a value that precedes 'Two';
        final int LAST_CARD = currentPlayerCards.length - 1;
        int CHECK_CARD_PLAYER_ONE = currentPlayerCards.length - 1;
        int CHECK_CARD_PLAYER_TWO = currentPlayerCards.length - 1;

        //first player check;
        if (currentPlayerCards[FIRST_CARD].charAt(CARD_TYPE) == CARD_TWO
                && currentPlayerCards[LAST_CARD].charAt(CARD_TYPE) == CARD_ACE) {
            CHECK_CARD_PLAYER_ONE--; // since the cards are sorted low to high, the last card is 'Ace',
            // however in this case 'Ace' is used as value 1 and it precedes 'Two'
            // which means that the higher card in this case will be the second last card;
        }

        //second player check;
        if (comparePlayerCards[FIRST_CARD].charAt(CARD_TYPE) == CARD_TWO
                && comparePlayerCards[LAST_CARD].charAt(CARD_TYPE) == CARD_ACE) {
            CHECK_CARD_PLAYER_TWO--; // since the cards are sorted low to high, the last card is 'Ace',
            // however in this case 'Ace' is used as value 1 and it precedes 'Two'
            // which means that the higher card in this case will be the second last card;
        }

        int currentPlayerHighestCard = currentPlayerCards[CHECK_CARD_PLAYER_ONE].charAt(CARD_TYPE);
        int comparePlayerHighestCard = comparePlayerCards[CHECK_CARD_PLAYER_TWO].charAt(CARD_TYPE);

        if (currentPlayerHighestCard > comparePlayerHighestCard) return WIN_GAME;
        if (currentPlayerHighestCard < comparePlayerHighestCard) return LOSS_GAME;

        return TIE_GAME;
    }


    /**
     * @param cards using its values the values are transferred to an array of Strings.
     *              The reason to do so is because thus it is easier and more compact
     *              to work with the data;
     **/
    private String[] convertToArray(String cards) {
        final String EMPTY_SPACE_DELIMITER = " "; // the delimiter that separates the cards from one another;
        cards = cards.toUpperCase();
        return cards.split(EMPTY_SPACE_DELIMITER);
    }

    /**
     * Since it's easier to work with sorted values for this problem using
     *
     * @param cards we sort the values using a simple bubble sort,
     *              which is implemented and using the {@link #cardValues}
     *              filled with all types of cards and their values respectively,
     *              Using the values inside of {@link #cardValues}
     *              the array being sorted;
     **/
    private void sortCardsByValue(String[] cards) {
        for (int i = 0; i < cards.length; i++) {
            for (int j = 0; j < cards.length - i - 1; j++) {
                int valueOne = this.cardValues.get(cards[j].charAt(CARD_TYPE));
                int valueTwo = this.cardValues.get(cards[j + 1].charAt(CARD_TYPE));
                if (valueOne > valueTwo) {
                    // simple swap;
                    String temp = cards[j];
                    cards[j] = cards[j + 1];
                    cards[j + 1] = temp;
                    // end of swap;
                }
            }
        }
    }

    /**
     * @param x is the first value that has to be taken into account
     * @param y is the second value that has to be taken into account
     * @return the higher of the two values
     **/
    private int getHigherValue(int x, int y) {
        return Math.max(x, y);
    }

    /**
     * @param x is the first value that has to be taken into account
     * @param y is the second value that has to be taken into account
     * @return the lower of the two values
     **/
    private int getLowerValue(int x, int y) {
        return Math.min(x, y);
    }

    /**
     * @param cardValues filling it with the card
     *                   characters and their respective values;
     **/
    private void setUpCardValues(HashMap<Character, Integer> cardValues) {
        cardValues.put(CARD_TWO, CARD_TWO_VALUE);
        cardValues.put(CARD_THREE, CARD_THREE_VALUE);
        cardValues.put(CARD_FOUR, CARD_FOUR_VALUE);
        cardValues.put(CARD_FIVE, CARD_FIVE_VALUE);
        cardValues.put(CARD_SIX, CARD_SIX_VALUE);
        cardValues.put(CARD_SEVEN, CARD_SEVEN_VALUE);
        cardValues.put(CARD_EIGHT, CARD_EIGHT_VALUE);
        cardValues.put(CARD_NINE, CARD_NINE_VALUE);
        cardValues.put(CARD_TEN, CARD_TEN_VALUE);
        cardValues.put(CARD_JACK, CARD_JACK_VALUE);
        cardValues.put(CARD_QUEEN, CARD_QUEEN_VALUE);
        cardValues.put(CARD_KING, CARD_KING_VALUE);
        cardValues.put(CARD_ACE, CARD_ACE_VALUE);
    }

    /**
     * @param cards using these cards the method
     *              finds which card has the highest value;
     **/
    private int getHighestValueCard(String[] cards) {
        int highestValueCard = CARD_TWO_VALUE; //CARD_TWO_VALUE has the lowest value in a
        // deck of cards(Not taking into account the 'Ace' card);
        for (String card : cards) {
            int currentValueCard = this.cardValues.get(card.charAt(CARD_TYPE));
            if (currentValueCard > highestValueCard) highestValueCard = currentValueCard;
        }
        return highestValueCard;
    }

    /**
     * @param cards           using these cards the method finds which card has the highest value
     * @param valueNotToCount is a value which may be in a pair and will not be taken into account;
     **/
    private char getHighestValueCard(String[] cards, int valueNotToCount) {
        int highestValueCard = CARD_TWO_VALUE; //CARD_TWO_VALUE has the lowest value in a deck of cards;
        for (String card : cards) {
            int currentValueCard = this.cardValues.get(card.charAt(CARD_TYPE));
            if (currentValueCard != valueNotToCount) {
                if (currentValueCard > highestValueCard) highestValueCard = currentValueCard;
            }

            if (currentValueCard != valueNotToCount) {
                if (currentValueCard > highestValueCard) highestValueCard = currentValueCard;
            }

        }
        return getCardType(highestValueCard);
    }

    /**@param value is used to present a value of card
     *            @return the type of that card;**/
    private char getCardType(int value) {
        if (value == CARD_ACE_VALUE) return CARD_ACE;
        if (value == CARD_KING_VALUE) return CARD_KING;
        if (value == CARD_QUEEN_VALUE) return CARD_QUEEN;
        if (value == CARD_JACK_VALUE) return CARD_JACK;
        if (value == CARD_TEN_VALUE) return CARD_TEN;
        if (value == CARD_NINE_VALUE) return CARD_NINE;
        if (value == CARD_EIGHT_VALUE) return CARD_EIGHT;
        if (value == CARD_SEVEN_VALUE) return CARD_SEVEN;
        if (value == CARD_SIX_VALUE) return CARD_SIX;
        if (value == CARD_FIVE_VALUE) return CARD_FIVE;
        if (value == CARD_FOUR_VALUE) return CARD_FOUR;
        if (value == CARD_THREE_VALUE) return CARD_THREE;
        return CARD_TWO;
    }


    /**@param type is used to present a type of card
     *            @return the value of that card;**/
    private int getCardValue(char type) {
        if (type == CARD_ACE) return CARD_ACE_VALUE;
        if (type == CARD_KING) return CARD_KING_VALUE;
        if (type == CARD_QUEEN) return CARD_QUEEN_VALUE;
        if (type == CARD_JACK) return CARD_JACK_VALUE;
        if (type == CARD_TEN) return CARD_TEN_VALUE;
        if (type == CARD_NINE) return CARD_NINE_VALUE;
        if (type == CARD_EIGHT) return CARD_EIGHT_VALUE;
        if (type == CARD_SEVEN) return CARD_SEVEN_VALUE;
        if (type == CARD_SIX) return CARD_SIX_VALUE;
        if (type == CARD_FIVE) return CARD_FIVE_VALUE;
        if (type == CARD_FOUR) return CARD_FOUR_VALUE;
        if (type == CARD_THREE) return CARD_THREE_VALUE;

        return CARD_TWO_VALUE; //which covers the exception in which the ace is used as value one;
    }


    /**
     * @param cards using these cards the method
     *              finds whether there is a pair;
     **/
    private boolean isPair(String[] cards) {
        List<Character> checkedCardsList = new ArrayList<>();
        for (String card : cards) {
            if (checkedCardsList.contains(card.charAt(CARD_TYPE))) {
                PAIR_CARD_TYPE = card.charAt(CARD_TYPE); // keep the type of card in case of a tie;
                PAIR_TYPE_SPARE_CARD = getHighestValueCard(cards, PAIR_CARD_TYPE);
                return true;
            } else checkedCardsList.add(card.charAt(CARD_TYPE));
        }
        return false;
    }

    /**
     * @param cards using these cards the method
     *              finds whether there are two pairs;
     **/
    private boolean areTwoPairs(String[] cards) {
        List<Character> checkedCardsList = new ArrayList<>();
        char firstPairType = ' '; // set and keep the type of card that is in the first pair;
        int secondPairCounter = 0;
        for (String card : cards) {
            if (checkedCardsList.contains(card.charAt(CARD_TYPE))) {
                if (card.charAt(CARD_TYPE) != firstPairType) {
                    secondPairCounter++;
                    if (secondPairCounter == NUMBER_OF_CARDS_IN_A_PAIR) {
                        TWO_PAIRS_SECOND_PAIR_TYPE = card.charAt(CARD_TYPE); // keep the second card in case of a tie;
                        TWO_PAIRS_FIRST_PAIR_TYPE = firstPairType;
                        return true;
                    }
                } else {
                    if (firstPairType == EMPTY_SPACE_DELIMITER) {
                        firstPairType = card.charAt(CARD_TYPE);
                        TWO_PAIRS_FIRST_PAIR_TYPE = card.charAt(CARD_TYPE); // keep the first card in case of a tie;
                    }
                }
            } else {
                checkedCardsList.add(card.charAt(CARD_TYPE));
            }
        }
        return false;
    }

    /**
     * @param cards using these cards the method
     *              finds whether there are three of a kind cards;
     **/
    private boolean areThreeOfAKind(String[] cards) {
        List<Character> checkedCardsList = new ArrayList<>();
        int threeOfAKindCounter = 1; //if we have the card stored in a list,
        // the counter should be 1 since the letter appears once already;
        for (String card : cards) {
            if (checkedCardsList.contains(card.charAt(CARD_TYPE))) {
                if (threeOfAKindCounter == THREE_OF_A_KIND_CARDS) {
                    THREE_OF_A_KIND_TYPE = card.charAt(CARD_TYPE);
                    THREE_OF_A_KIND_TYPE_SPARE_CARD = getCardType(getHighestValueCard(cards, THREE_OF_A_KIND_TYPE));
                    return true;
                }
                threeOfAKindCounter++;
            } else checkedCardsList.add(card.charAt(CARD_TYPE));
        }
        return false;
    }

    /**
     * @param cards using these cards the method
     *              finds whether there are four of a kind cards;
     **/
    private boolean areFourOfAKind(String[] cards) {
        List<Character> checkedCardsList = new ArrayList<>();
        final int FOUR_OF_A_KIND_CARDS = 4;
        int fourOfAKindCounter = 1;//if we have the card stored in a list,
        // the counter should be 1 since the letter appears once already;
        for (String card : cards) {
            if (checkedCardsList.contains(card.charAt(CARD_TYPE))) {
                if (fourOfAKindCounter == FOUR_OF_A_KIND_CARDS) {
                    FOUR_OF_A_KIND_TYPE = card.charAt(CARD_TYPE);
                    return true;
                }
                fourOfAKindCounter++;
            } else checkedCardsList.add(card.charAt(CARD_TYPE));
        }
        return false;
    }

    /**
     * @param cards using these cards the method
     *              finds whether there is a Straight in a player's hand;
     **/
    private boolean isStraight(String[] cards) {
        final int INCREASE_VALUE_WITH_ONE = 1; // there is a straight if there is
        //a sequence of 5 cards in increasing value (Ace can precede 2 and follow up King), where increasing value is 1;
        //For example, 2H 3S 4C 5H 6S is a straight;

        boolean containsAceAndTwo = false;
        int cardValuesLength = cards.length - 1;
        int currentCardValue, nextCardValue;
        if (cards[FIRST_CARD].charAt(CARD_TYPE) == CARD_TWO
                && cards[cards.length - 1].charAt(CARD_TYPE) == CARD_ACE) {
            containsAceAndTwo = true;
            cardValuesLength--; // if a player's hand contains 'Ace' and 'Two'
            // there is a chance that the ace could be used to precede 'Two'
        }
        for (int i = 0; i < cardValuesLength; i++) {
            currentCardValue = this.cardValues.get(cards[i].charAt(CARD_TYPE));
            nextCardValue = this.cardValues.get(cards[i + 1].charAt(CARD_TYPE));

            boolean compare = (currentCardValue + INCREASE_VALUE_WITH_ONE) != nextCardValue;
            if (compare) return false;
        }

        if (!containsAceAndTwo) {
            // else check whether the last card matches with the rest and there is a straight flush;
            final int SECOND_LAST_CARD = cards.length - 2, LAST_CARD = cards.length - 1;

            currentCardValue = this.cardValues.get(cards[SECOND_LAST_CARD].charAt(CARD_TYPE));
            nextCardValue = this.cardValues.get(cards[LAST_CARD].charAt(CARD_TYPE));

            boolean compare = (currentCardValue + INCREASE_VALUE_WITH_ONE) != nextCardValue;

            if (compare) return false;
        }

        return true;
    }

    /**
     * @param cards using these cards the method
     *              finds whether there is a Full House in a player's hand;
     **/
    private boolean isFullHouse(String[] cards) {
        List<Character> checkedCardsList = new ArrayList<>();
        final int FIRST_CARD = 0;
        char firstType = cards[FIRST_CARD].charAt(CARD_TYPE), secondType = EMPTY_SPACE_DELIMITER;
        int firstCardTypeCounter = 1, secondCardTypeCounter = 1;
        //if we have the card stored in a list,
        // the counter should be 1 since the letter appears once already;
        for (String card : cards) {
            if (checkedCardsList.contains(card.charAt(CARD_TYPE))) {
                if (card.charAt(CARD_TYPE) == firstType) {
                    firstCardTypeCounter++;
                } else {
                    if (secondType == EMPTY_SPACE_DELIMITER) {
                        secondType = card.charAt(CARD_TYPE);
                    }
                    secondCardTypeCounter++;
                }
            } else {
                checkedCardsList.add(card.charAt(CARD_TYPE));
            }
        }

        final int cardTypeOne = Math.max(firstCardTypeCounter, secondCardTypeCounter); //three of a kind type of card;
        final int cardTypeTwo = Math.min(firstCardTypeCounter, secondCardTypeCounter); // the pair type of card;
        //check to see whether there is a full house;
        if (cardTypeOne == THREE_OF_A_KIND_CARDS && cardTypeTwo == NUMBER_OF_CARDS_IN_A_PAIR) {
            if (firstCardTypeCounter == THREE_OF_A_KIND_CARDS) {
                FULL_HOUSE_THREE_OF_A_KIND_TYPE = firstType;
                FULL_HOUSE_PAIR_TYPE = secondType;
            } else {
                FULL_HOUSE_THREE_OF_A_KIND_TYPE = secondType;
                FULL_HOUSE_PAIR_TYPE = firstType;
            }
            return true;
        }
        return false;
    }

    /**
     * @param cards using these cards the method
     *              finds whether there is a Royal Flush in a player's hand;
     **/
    private boolean isRoyalFlush(String[] cards) {
        final int INCREASE_VALUE_WITH_ONE = 1, FIRST_CARD = 0; // there is a Royal Flush there is
        //a sequence of 5 cards in increasing value, where increasing value is 1;
        //For example, TH JH QH KH AH is a Royal Flush;
        if (this.cardValues.get(cards[FIRST_CARD].charAt(CARD_TYPE)) != CARD_TEN_VALUE) {
            return false; // Royal flush starts with card 10 ('T')
            // followed by a Jack('T'), Queen('Q'), King('K'),
            // and Ace('A'); all of them of the same suit;
        }

        final char MAIN_CARD_SUIT = cards[FIRST_CARD].charAt(CARD_SUIT); // must not be different, otherwise it is not flush;

        for (int i = 0; i < cards.length - 1; i++) {
            int currentCardValue = this.cardValues.get(cards[i].charAt(CARD_TYPE));
            int nextCardValue = this.cardValues.get(cards[i + 1].charAt(CARD_TYPE));

            boolean firstCheck = (currentCardValue + INCREASE_VALUE_WITH_ONE) != nextCardValue;
            boolean secondCheck = cards[i + 1].charAt(CARD_SUIT) != MAIN_CARD_SUIT;

            if (firstCheck || secondCheck) return false;
        }
        return true;
    }

    /**
     * @param cards using these cards the method
     *              finds whether there is a Straight Flush in a player's hand;
     **/
    private boolean isStraightFlush(String[] cards) {
        final int INCREASE_VALUE_WITH_ONE = 1; // there is a straight flush if there is
        //a sequence of 5 cards in increasing value
        // (Ace can precede 2 and follow up King), where increasing value is 1;
        //For example, 2H 3H 4H 5H 6H is a straight flush
        final char MAIN_CARD_SUIT = cards[FIRST_CARD].charAt(CARD_SUIT); // must not be different, otherwise it is not flush;

        boolean containsAceAndTwo = false; // where 'Ace' is the type of card and 'Two' is other type of card;
        int cardValuesLength = cards.length - 1;
        // since the loop check current card and the card after
        // it on each iteration the length is set to be length - 1;

        if (cards[FIRST_CARD].charAt(CARD_TYPE) == CARD_TWO
                && cards[cards.length - 1].charAt(CARD_TYPE) == CARD_ACE) {
            containsAceAndTwo = true;
            cardValuesLength--; // if a player's hand contains 'Ace' and 'Two'
            // there is a chance that the ace could be used to precede 'Two'
        }

        int currentCardValue, nextCardValue;
        boolean firstCheck, secondCheck;
        for (int i = 0; i < cardValuesLength; i++) {
            currentCardValue = this.cardValues.get(cards[i].charAt(CARD_TYPE));
            nextCardValue = this.cardValues.get(cards[i + 1].charAt(CARD_TYPE));

            firstCheck = (currentCardValue + INCREASE_VALUE_WITH_ONE) != nextCardValue;
            secondCheck = cards[i + 1].charAt(CARD_SUIT) != MAIN_CARD_SUIT;

            if (firstCheck || secondCheck) {
                return false;
            }
        }
        if (!containsAceAndTwo) {
            // else check whether the last card matches with the second last and there is a straight flush;
            final int SECOND_LAST_CARD = cards.length - 2, LAST_CARD = cards.length - 1;
            currentCardValue = this.cardValues.get(cards[SECOND_LAST_CARD].charAt(CARD_TYPE));
            nextCardValue = this.cardValues.get(cards[LAST_CARD].charAt(CARD_TYPE));

            firstCheck = (currentCardValue + INCREASE_VALUE_WITH_ONE) != nextCardValue;
            secondCheck = cards[LAST_CARD].charAt(CARD_SUIT) != MAIN_CARD_SUIT;

            if (firstCheck || secondCheck) return false;
        }

        return true;// since we reached this statement and there is 'Ace'
        // (holding the highest value,therefore it is in the end of the array);
        // the ace becomes with value one and it precedes 'Two';
    }

    /**
     * @param cards using these cards the method
     *              finds whether there is a Flush in a player's hand;
     **/
    private boolean isFlush(String[] cards) {
        final int FIRST_CARD = 0; // there is a flush if there are 5 cards of the same suit;
        //For example, 2H 4H 6H TH KH is a flush
        final char MAIN_CARD_SUIT = cards[FIRST_CARD].charAt(CARD_SUIT); // must not be different, otherwise it is not flush;
        //check whether all cards are of the same suit:
        return IntStream.range(0, cards.length)
                .noneMatch(i -> cards[i].charAt(CARD_SUIT) != MAIN_CARD_SUIT);
    }

    private String getCards() {
        return cards;
    }

    private char getPAIR_CARD_TYPE() {
        return PAIR_CARD_TYPE;
    }

    private char getPAIR_TYPE_SPARE_CARD() {
        return PAIR_TYPE_SPARE_CARD;
    }

    private char getTWO_PAIRS_FIRST_PAIR_TYPE() {
        return TWO_PAIRS_FIRST_PAIR_TYPE;
    }

    private char getTWO_PAIRS_SECOND_PAIR_TYPE() {
        return TWO_PAIRS_SECOND_PAIR_TYPE;
    }

    private char getTHREE_OF_A_KIND_TYPE() {
        return THREE_OF_A_KIND_TYPE;
    }

    private char getTHREE_OF_A_KIND_TYPE_SPARE_CARD() {
        return THREE_OF_A_KIND_TYPE_SPARE_CARD;
    }

    private char getFULL_HOUSE_THREE_OF_A_KIND_TYPE() {
        return FULL_HOUSE_THREE_OF_A_KIND_TYPE;
    }

    private char getFULL_HOUSE_PAIR_TYPE() {
        return FULL_HOUSE_PAIR_TYPE;
    }

    private char getFOUR_OF_A_KIND_TYPE() {
        return FOUR_OF_A_KIND_TYPE;
    }

}

