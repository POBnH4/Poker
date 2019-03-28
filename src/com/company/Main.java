package com.company;

public class Main {

    public static void main(String[] args) {

        final String PLAYER_ONE_NAME = "Peter", PLAYER_TWO_NAME = "Not Peter";
        //NOTE: The format of the cards has to be a string, for example -> "4S 5S 8C AS AD"**/
        //final String PLAYER_ONE_HAND = "TH AH JH QH KH";
        final String PLAYER_ONE_HAND = "TH JH QH KH AH";
        final String PLAYER_TWO_HAND = "2D 4H 6C 8H TH";
        //TH JH QH KH AH - royal flush;
        //2D 4H 6C 8H TH - high card;

        //NOTE: IF testing individual methods,
        //FIRST SORT THE CARDS using the sort method in PokerHand class;
        PokerHand pokerPlayer = new PokerHand(PLAYER_ONE_NAME,PLAYER_ONE_HAND);
        PokerHand pokerPlayerTwo = new PokerHand(PLAYER_TWO_NAME,PLAYER_TWO_HAND);

        //NOTE: return value equals whether the first player wins/losses/ties, in
        //the following example: pokerPlayer.compareWith(pokerPlayerTwo)
        //pokerPlayer is the first player(Win = 1, Loss = 2, Tie = 3);
        System.out.println(pokerPlayer.compareWith(pokerPlayerTwo));

    }
}
