/*
 * STUDENTS: Jason Pettit, Sergio Quiroz, Marcus Gonzalez,
 *           Adam Houser, Colin Reed
 * COURSE: CST 338-30_SU19
 * EXERCISE: Module 6 Timed High-Card Game
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.text.*;

public class Assignment6
{
   static int MAX_CARDS_PER_HAND = 56;
   static int NUM_PLAYERS = 2;
   static int NUM_CARDS_PER_HAND = 7;
   
   public static void main(String[] args)
   {
      int numPacksPerDeck = 1;
      int numJokersPerPack = 2;
      int numUnusedCardsPerPack = 0;
      int numOfPlayers = NUM_PLAYERS;
      int cardsPerHand = NUM_CARDS_PER_HAND;
      Card[] unusedCardsPerPack = null;
      
      CardGameFramework highCardGame = new CardGameFramework( 
            numPacksPerDeck, numJokersPerPack,  
            numUnusedCardsPerPack, unusedCardsPerPack, 
            numOfPlayers, cardsPerHand);
      
      ClockTimer timer = new ClockTimer();
      timer.startTimer();
      
      GameModel model = new GameModel(highCardGame, "Computer", "Player");
      GameView view = new GameView(timer);
      
      GameControl game = new GameControl(model, view);
   }
}

//START class Card
/* 
 * The Card class allows for the representation and manipulation of a single
 * playing card as found in a standard 56 card deck.
 */
class Card
{
   /*
    * A public enum Suit stores the values of clubs, diamonds, hearts, spades;
    * A public static final char cValue[] stores the values of each card 1-9 and
    * T-A.  Ten is represented by 'T', not '10'.
    */
   public enum Suit
   {
      clubs, diamonds, hearts, spades;
   }
   public static final char cValue[] = {'A', '2', '3', '4', '5', '6', '7', '8',
         '9', 'T', 'J', 'Q', 'K', 'X'};
   public static char[] valuRanks = {'A', '2', '3', '4', '5', '6', '7', '8',
         '9', 'T', 'J', 'Q', 'K', 'X'};
   private char value;
   private Suit suit;
   private boolean errorFlag;

   //START constructors
   //Default constructor
   public Card()
   {
      this.set('A', Suit.spades);
   }

   //Constructor
   public Card(char value, Suit suit)
   {
      this.set(value, suit);
   }
   //END constructors

   //START mutators
   public boolean set(char value, Suit suit)
   {
      if(isValid(value, suit))
      {
         this.value = value;
         this.suit = suit;
         this.errorFlag = false;
      }
      else
      {
         this.errorFlag = true;
      }

      return this.errorFlag;
   }
   //END mutators

   //START accessors
   public Suit getSuit()
   {
      return this.suit;
   }

   public char getValue()
   {
      return this.value;
   }

   public boolean getErrorFlag()
   {
      return this.errorFlag;
   }
   //END accessors

   /*
    * public boolean equals(Card card) returns true if all the fields are
    * identical and false otherwise.
    */
   public boolean equals(Card card)
   {
      boolean isEqual = false;

      if (this.getValue() == card.getValue() && this.getSuit() == card.getSuit()
            && this.getErrorFlag() == card.getErrorFlag())
      {
         isEqual = true;
      }
      return isEqual;
   }

   /*
    * private boolean isValid(char value, Suit suit) returns true if value is a
    * valid card value and false otherwise.
    */
   private boolean isValid(char value, Suit suit)
   {
      boolean isValid = false;

      for (char index : cValue)
      {
         if (Character.toUpperCase(value) == index)
         {
            isValid = true;
         }
      }
      return isValid;
   }

   /*
    * toString() concatenates the A thru 2 card value and the card suit into a
    * single string "value of suit" example: "A of clubs"
    */
   public String toString()
   {
      if(errorFlag == true)
      {
         return "[invalid]";
      }
      else
      {
         String card = Character.toUpperCase(value) + " of " + suit;
         return card;
      }
    }

   static void arraySort(Card[] cArray, int arraySize)
   {
      int tempVal1 = 0;
      int tempVal2 = 0;

      for (int i = 0; i < arraySize - 1; i++)
      {
         for (int j = 0; j < arraySize - i - 1; j++)
         {
            for (int x = 0; x < valuRanks.length; x++)
            {
               if(valuRanks[x] == cArray[j].getValue())
               {
                  tempVal1 = x;
                  break;
               }
            }
            
            for (int y = 0; y < valuRanks.length; y++)
            {
               if(valuRanks[y] == cArray[j + 1].getValue())
               {
                  tempVal2 = y;
                  break;
               }
            }
               if (tempVal1 > tempVal2)
               {
                  Card temp = cArray[j];
                  cArray[j] = cArray[j+1];
                  cArray[j+1] = temp;
               }
            }
         }
   }
}
//END class Card

//START class Hand
/**
 * The Hand class represents the cards held by a single player
 */
class Hand
{
   public static final int MAX_CARDS = 56;

   private Card[] myCards;
   private int numCards;

   //START constructors
   //Default constructor
   public Hand()
   {
      this.myCards = new Card[MAX_CARDS];
      this.numCards = 0;
   }
   //END constructors
  
   // public void resetHand() resets an existing hand to 0 cards
   public void resetHand()
   {
      this.numCards = 0;
   }

   /*
    * public boolean takeCard(Card card) puts a new card in the players hand
    * and also checks that taking a new card would not violate the MAX_CARDS
    * allowed in the hand, which is set to MAX_CARDS = 56 by the Hand constructor.
    */
   public boolean takeCard(Card card)
   {
      boolean newCard = false;

      //checking if hand size plus card drawn will put us over max size
      if (numCards + 1 <= MAX_CARDS)
      {
         myCards[numCards] = new Card(card.getValue(), card.getSuit());
         this.numCards++;
         newCard = true;
      }
      return newCard;
   }

   /*
    * public Card playCard() returns and removes the card in the top occupied
    * position of the hand array.
    */
   public Card playCard(int cardIndex)
   {
      if ( numCards == 0 ) //error
      {
         //Creates a card that does not work
         return new Card('M', Card.Suit.spades);
      }
      //Decreases numCards.
      Card card = myCards[cardIndex];

      numCards--;
      for(int i = cardIndex; i < numCards; i++)
      {
         myCards[i] = myCards[i+1];
      }

      myCards[numCards] = null;

      return card;
    }

   // toString() concatenates the cards in the hand into a single string.
   public String toString()
   {
      int cardCounter = 0;
      int handCards = 0;
      StringBuilder hand = new StringBuilder("Hand = (");

      if (this.numCards > 0)
      {
         for (Card card:myCards)
         {
            if (card == null)
            {
               break;
            }
            hand.append(card.toString());
            if(handCards < this.numCards - 1)
            {
               hand.append(", ");
            }
            cardCounter++;
            handCards++;

            //if statement below formats the output onto multiple lines
            if (cardCounter % 5 == 0 && handCards != this.numCards)
            {
               hand.append("\n");
               cardCounter = 0;
            }
         }
      }
      hand.append(")\n");
      return hand.toString();
   }
   
   //Getter getNumCards returns the number of cards currently in the hand
   public int getNumCards()
   {
      return this.numCards;
   }

   // inspectCard accesses individual card (k).
   public Card inspectCard(int k)
   {
      if (this.myCards[k] != null)
      {
         return this.myCards[k];
      }
      else
      {
         Card tempCard = new Card('z', Card.Suit.clubs);
         return tempCard;
      }
   }

   public void sort()
   {
      Card.arraySort(this.myCards, this.numCards);
   }
}
//END class Hand

//START class CardGameFramework - provided by Instructor--------------------------
class CardGameFramework
{
 private static final int MAX_PLAYERS = 50;

 private int numPlayers;
 private int numPacks;                 // # standard 52-card packs per deck
                                       // ignoring jokers or unused cards
 private int numJokersPerPack;         // if 2 per pack & 3 packs per deck, get 6
 private int numUnusedCardsPerPack;    // # cards removed from each pack
 private int numCardsPerHand;          // # cards to deal each player
 private Deck deck;                    // holds the initial full deck and gets
                                       // smaller (usually) during play
 private Hand[] hand;                  // one Hand for each player
 private Card[] unusedCardsPerPack;    // an array holding the cards not used
                                       // in the game.  e.g. pinochle does not
                                       // use cards 2-8 of any suit

 public CardGameFramework( int numPacks, int numJokersPerPack,
       int numUnusedCardsPerPack,  Card[] unusedCardsPerPack,
       int numPlayers, int numCardsPerHand)
 {
    int k;

    // filter bad values
    if (numPacks < 1 || numPacks > 6)
       numPacks = 1;
    if (numJokersPerPack < 0 || numJokersPerPack > 4)
       numJokersPerPack = 0;
    if (numUnusedCardsPerPack < 0 || numUnusedCardsPerPack > 50) //  > 1 card
       numUnusedCardsPerPack = 0;
    if (numPlayers < 1 || numPlayers > MAX_PLAYERS)
       numPlayers = 4;
    
    // one of many ways to assure at least one full deal to all players
    if  (numCardsPerHand < 1 ||
          numCardsPerHand >  numPacks * (52 - numUnusedCardsPerPack)
          / numPlayers )
       numCardsPerHand = numPacks * (52 - numUnusedCardsPerPack) / numPlayers;

    // allocate
    this.unusedCardsPerPack = new Card[numUnusedCardsPerPack];
    this.hand = new Hand[numPlayers];
    for (k = 0; k < numPlayers; k++)
       this.hand[k] = new Hand();
    deck = new Deck(numPacks);

    // assign to members
    this.numPacks = numPacks;
    this.numJokersPerPack = numJokersPerPack;
    this.numUnusedCardsPerPack = numUnusedCardsPerPack;
    this.numPlayers = numPlayers;
    this.numCardsPerHand = numCardsPerHand;
    for (k = 0; k < numUnusedCardsPerPack; k++)
       this.unusedCardsPerPack[k] = unusedCardsPerPack[k];

    // prepare deck and shuffle
    newGame();
 }

 // constructor overload/default for game like bridge
 public CardGameFramework()
 {
    this(1, 0, 0, null, 4, 13);
 }

 public Hand getHand(int k)
 {
    // hands start from 0 like arrays

    // on error return automatic empty hand
    if (k < 0 || k >= numPlayers)
       return new Hand();

    return hand[k];
 }

 public Card getCardFromDeck() { return deck.dealCard(); }

 public int getNumCardsRemainingInDeck() { return deck.getNumCards(); }

 public void newGame()
 {
    int k, j;

    // clear the hands
    for (k = 0; k < numPlayers; k++)
       hand[k].resetHand();

    // restock the deck
    deck.init(numPacks);

    // remove unused cards
    for (k = 0; k < numUnusedCardsPerPack; k++)
       deck.removeCard( unusedCardsPerPack[k] );

    // add jokers
    for (k = 0; k < numPacks; k++)
       for ( j = 0; j < numJokersPerPack; j++)
          deck.addCard( new Card('X', Card.Suit.values()[j]) );

    // shuffle the cards
    deck.shuffle();
 }

 public boolean deal()
 {
    // returns false if not enough cards, but deals what it can
    int k, j;
    boolean enoughCards;

    // clear all hands
    for (j = 0; j < numPlayers; j++)
       hand[j].resetHand();

    enoughCards = true;
    for (k = 0; k < numCardsPerHand && enoughCards ; k++)
    {
       for (j = 0; j < numPlayers; j++)
          if (deck.getNumCards() > 0)
             hand[j].takeCard( deck.dealCard() );
          else
          {
             enoughCards = false;
             break;
          }
    }

    return enoughCards;
 }

 void sortHands()
 {
    int k;

    for (k = 0; k < numPlayers; k++)
       hand[k].sort();
 }

 Card playCard(int playerIndex, int cardIndex)
 {
    // returns bad card if either argument is bad
    if (playerIndex < 0 ||  playerIndex > numPlayers - 1 ||
        cardIndex < 0 || cardIndex > numCardsPerHand - 1)
    {
       //Creates a card that does not work
       return new Card('M', Card.Suit.spades);      
    }
 
    // return the card played
    return hand[playerIndex].playCard(cardIndex);
 
 }


 boolean takeCard(int playerIndex)
 {
    // returns false if either argument is bad
    if (playerIndex < 0 || playerIndex > numPlayers - 1)
       return false;
   
     // Are there enough Cards?
     if (deck.getNumCards() <= 0)
        return false;

     return hand[playerIndex].takeCard(deck.dealCard());
 }
}
//END class CardGameFramework  ---------------------------------------------------
