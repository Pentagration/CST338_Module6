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
import javax.swing.JOptionPane;

import com.sun.media.sound.ModelAbstractChannelMixer;

import jdk.internal.module.IllegalAccessLogger.Mode;

import java.util.*;
import java.text.*;

public class Assignment6
{
   static int NUM_CARDS_PER_PACK = 52;    //standard pack without jokers
   static int MAX_CARDS_PER_HAND = 56;
   static int NUM_PLAYERS = 2;
   static int NUM_CARDS_PER_HAND = 7;
   
   public static void main(String[] args)
   {  
      int numPacksPerDeck = 1;
      int numJokersPerPack = 4;
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

//START class ClockTimer
class ClockTimer extends JPanel implements ActionListener, Runnable
{
 int count=0;
 boolean pauseStatus=false; 
 public JButton timerButton;
 public JLabel timerLabel;
 public JPanel timerPanel;
  
  public ClockTimer()
  {  
     setLayout(new BorderLayout());
     
     timerPanel = new JPanel();
     timerPanel.setLayout(new FlowLayout());
     
     timerButton = new JButton("Start/Stop");
     timerButton.setPreferredSize(new Dimension(200,50));
     
     timerButton.addActionListener(this);
     timerPanel.add(timerButton);
     add(timerPanel, "South");
     
     timerLabel = new JLabel();
     timerLabel.setHorizontalAlignment(JLabel.CENTER);
     timerLabel.setFont(timerLabel.getFont().deriveFont(24.0f));
     timerLabel.setText("0");
     add(timerLabel, "Center");
  }
  
  public void startTimer() 
  {
     Thread timerThread = new Thread(this);
     timerThread.start();
  }
  
  public String formatTimer(long seconds) 
  {
     long sec = seconds % 60;
     long min = (seconds / 60) % 60;
     return String.format("%02d:%02d", min, sec);
  }
  
  public void actionPerformed(ActionEvent e) 
  {
     pauseStatus = !pauseStatus;
  }
  
  public void run()
  {
     while (true)
     {
        if (!pauseStatus)
        {
           count++;
        }
     timerLabel.setText(formatTimer(count));
     doNothing();
     }
  }
  
  public void doNothing()
  {
     try 
     {
        Thread.sleep(1000);
     } 
     catch (InterruptedException e) 
     {
        System.out.println(e);
     }
  }
}
//END class ClockTimer

//START class GameModel
class GameModel
{
   public CardGameFramework highCardGame;
   private String computer;
   private String human;
   private int computerNum = 0;
   private int humanNum = 1;
   private Card leftCard;
   private Card rightCard;
   public int computerScore = 0;
   public int humanScore = 0;
   private boolean computerCantPlay = false;
   private boolean humanCantPlay = false;
   JLabel[] computerLabels = new JLabel[GameView.NUM_CARDS_PER_HAND];
   JLabel[] humanLabels = new JLabel[GameView.NUM_CARDS_PER_HAND];
   public ButtonGroup cardSelected;
   
   public GameModel()
   {
      this.highCardGame = null;
      this.computer = "";
      this.human = "";
      cardSelected = new ButtonGroup();
   }
   
   public GameModel(CardGameFramework highCardGame, String computer, String human)
   {
      this.highCardGame = highCardGame;
      this.computer = computer;
      this.human = human;
      cardSelected = new ButtonGroup();
   }

   // deal cards
   public void dealCards()
   {
      this.highCardGame.deal();
   }
   
   // need to play a card
   public Card playCard(int player, int card)
   {
      return this.highCardGame.playCard(player, card);
   }
   
   //check if the card is playable +/- 1
   public int getDifference(Card card, Card card2)
   {
      int val1 = 0, val2 = 0;
      for (int i = 0; i < Card.cValue.length; i++)
      {
         if (Card.cValue[i] == card.getValue())
            val1 = i;
         else if (Card.cValue[i] == card2.getValue())
            val2 = i;   
      }
      return Math.abs(val1 - val2);
   }
   
   // need to take a card to replace cards played
   public boolean takeCard(int player)
   {
      return this.highCardGame.takeCard(player);
   }
   
   // setter and getter for each game play area
   public Card getLeftCard()
   {
      return leftCard;
   }
   
   public Card getRightCard()
   {
      return rightCard;
   }
   
   public void setLeftCard(Card card)
   {
      leftCard = card;
   }
   
   public void setRightCard(Card card)
   {
      rightCard = card;
   }
   
   // setter and getter for each player can't play
   public boolean getComputerPlay()
   {
      return computerCantPlay;
   }
   
   public boolean getHumanPlay()
   {
      return humanCantPlay;
   }
   
   public void setComputerPlay(boolean bool)
   {
      computerCantPlay = bool;
   }
   
   public void setHumanPlay(boolean bool)
   {
      humanCantPlay = bool;
   }
   
   // new center cards when both can't play
   public void newMiddleCards()
   {
      leftCard = this.highCardGame.getCardFromDeck();
      rightCard = this.highCardGame.getCardFromDeck();
   }
   
   // update can't play
   public void cantPlay(int player)
   {
      if(player == 0)
      {
         computerScore++;
      }
      else if (player == 1)
      {
         humanScore++;
      }
      else
      {
         System.out.println("Bad player number");
      }
   }
   
   public void setTable(JPanel cpu, JPanel player, JPanel table)
   {
      // draw cards if needed
      if (highCardGame.getHand(1).getNumCards() < GameView.NUM_CARDS_PER_HAND)
      {
         takeCard(1);
      }
      
      if (highCardGame.getHand(0).getNumCards() < GameView.NUM_CARDS_PER_HAND)
      {
         //highCardGame.getHand(0).takeCard(highCardGame.getCardFromDeck());
         takeCard(0);
      }
      
      // create panels and add to table
      for (int k = 0; k < highCardGame.getHand(1).getNumCards(); k++)
      {
         humanLabels[k] = new JLabel(GUICard.getIcon(highCardGame.getHand(1)
               .inspectCard(k)));
         player.add(humanLabels[k]);
      }
      for (int i = 0; i < highCardGame.getHand(0).getNumCards(); i++) 
      {
         computerLabels[i] = new JLabel(GUICard.getBackCardIcon());
         cpu.add(computerLabels[i]);
      }

      if (getLeftCard() == null)
         {
         setLeftCard(highCardGame.getCardFromDeck());
         }
      if (getRightCard() == null)
         {
         setRightCard(highCardGame.getCardFromDeck());
         }
      table.add(new JButton(GUICard.getIcon(getLeftCard())));
      table.add(new JButton(GUICard.getIcon(getRightCard()))); //add 2 cards to table
      
      refreshButtons(player);
   }
   
   public void refreshButtons(JPanel player)
   {
      cardSelected = new ButtonGroup();

      for (int k = 0; k < highCardGame.getHand(1).getNumCards(); k++)
      {
         JRadioButton rb = new JRadioButton();
         rb.setHorizontalAlignment(JRadioButton.CENTER);
         rb.setActionCommand(Integer.toString(k));
         cardSelected.add(rb);
         player.add(rb);
      }
   }
   
   public void computerPlay() 
   {
	  computerCantPlay = true;
      for (int i = 0; i < highCardGame.getHand(0).getNumCards(); i++) 
      {
         if (getDifference(getLeftCard(), highCardGame.getHand(0).inspectCard(i)) == 1)
         {
            computerCantPlay = false;
            setLeftCard(highCardGame.getHand(0).inspectCard(i));
            playCard(0, i);
         } 
         else if (getDifference(getRightCard(), highCardGame.getHand(0).inspectCard(i)) == 1)
         {
            computerCantPlay = false;
            setRightCard(highCardGame.getHand(0).inspectCard(i));
            playCard(0, i);
         }
      }
      if (computerCantPlay) 
      {
         cantPlay(0);
         setComputerPlay(true);
      }
   }
   
   public void computerPlay(GameView view) 
   {
      computerCantPlay = true;
      for (int i = 0; i < highCardGame.getHand(0).getNumCards(); i++) 
      {
         if (getDifference(getLeftCard(), highCardGame.getHand(0).inspectCard(i)) == 1)
         {
            computerCantPlay = false;
            setLeftCard(highCardGame.getHand(0).inspectCard(i));
            playCard(0, i);
         } 
         else if (getDifference(getRightCard(), highCardGame.getHand(0).inspectCard(i)) == 1)
         {
            computerCantPlay = false;
            setRightCard(highCardGame.getHand(0).inspectCard(i));
            playCard(0, i);
         }
      }
      if (computerCantPlay) 
      {
         cantPlay(0);
         setComputerPlay(true);
         view.addMessage(humanScore,computerScore);
      }
   }
   
   public void turns(JPanel cpu, JPanel player, JPanel table) 
   {
      // if both can't play
      if (humanCantPlay && computerCantPlay) 
      {
         // check cards remaining in deck
         if (highCardGame.getNumCardsRemainingInDeck() > 1) 
         {
            humanCantPlay = false;
            computerCantPlay = false;
            newMiddleCards();
         } 
         else 
         {
            // end game condition if cards run out of deck
            // cards would run out during deal if only 1 left
            endGame();
         }
      } 
   }
   
   public void endGame()
   {
      String message = "";
      
      if (computerScore > humanScore)
      {
         message = "You Win!";
      }
      else if (humanScore > computerScore)
      {
         message = "Computer Wins!";
      }
      else
      {
         message = "Can you believe it, it's a tie!";
      }
      
      JOptionPane.showMessageDialog(null,
            message, "Game Over",
            JOptionPane.INFORMATION_MESSAGE);
   }
}
//END class GameModel

//START class GameView
class GameView extends JFrame 
{
   private ClockTimer timer;
   
   static final int MAX_CARDS_PER_HAND = 56;
   static final int NUM_PLAYERS = 2;
   static final int NUM_CARDS_PER_HAND = 7;

   int k;
   
   //main GUI panels
   public JPanel 
   pnlComputerHand,  //computer cards
   pnlHumanHand,     //human/player cards
   pnlPlayArea,      //two piles of cards
   pnlTime,          //timer and start/stop time button
   pnlGame;          //game messages, cannot play, and quit

   //GUI buttons
   public JButton
   quit,             //quit the game
   cannotPlay;       //when no card can be played
   
   public boolean cantPlayPress;
   
   //games messages
   public JTextArea message;
   
   public GameView(ClockTimer timer)
   {
      //setup main frame
      super();
      setTitle("Card Table");
      setLayout(new BorderLayout());
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

      this.timer = timer;
      cantPlayPress = false;
      //computer panel
      pnlComputerHand = new JPanel(new GridLayout(1,7));
      pnlComputerHand.setBorder(BorderFactory.createTitledBorder("Computer Hand"));

      //play area panel
      pnlPlayArea = new JPanel(new GridLayout(1,2));
      pnlPlayArea.setBorder(BorderFactory.createTitledBorder("Playing Area"));

      
      //human area panel
      pnlHumanHand = new JPanel(new GridLayout(2,7));
      pnlHumanHand.setBorder(BorderFactory.createTitledBorder("Your Hand"));

      //time panel
      GridBagConstraints gbc = new GridBagConstraints();
      
      pnlTime = new JPanel(new GridBagLayout());
      pnlTime.setBorder(BorderFactory.createTitledBorder("Time"));
      
      gbc.gridx = 0;
      gbc.gridy = 0;
      pnlTime.add(this.timer, gbc);

      //game panel
      pnlGame = new JPanel(new GridBagLayout());
      pnlGame.setBorder(BorderFactory.createTitledBorder("Game Panel"));
      
      this.message = new JTextArea("Welcome to the game High-Card. \n\n(1) Play a "
            + "card from Your Hand onto one of the cards the Playing Area. \n\n(2)"
            + " You can play a card from your hand that is one higher or one lower"
            + " than one of the cards in Playing Area. \n\n(3) If you cannot play,"
            + " click the Can't Play button. \n\nThe game ends when the deck is "
            + "out of cards. The player with the fewest number of Can't Play"
            + " clicks is the winner! \n\n");
      this.message.setEditable(false);
      this.message.setLineWrap(true);
      this.message.setWrapStyleWord(true);
      JScrollPane scrollV = new JScrollPane (message, 
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      scrollV.setPreferredSize(new Dimension(300,400));
      gbc.gridx = 0;
      gbc.gridy = 0;
      pnlGame.add(scrollV,gbc);
      
      this.quit = new JButton("Quit");
      this.quit.setPreferredSize(new Dimension(200,50));
      gbc.gridx = 0;
      gbc.gridy = 1;
      this.pnlTime.add(quit, gbc);
      
      this.cannotPlay = new JButton("Can't Play");
      this.cannotPlay.setPreferredSize(new Dimension(200,50));
      gbc.gridx = 0;
      gbc.gridy = 2;
      this.pnlTime.add(cannotPlay, gbc);
      
      //add all the major panels to the JFrame
      this.setSize(1200,800);
      this.add(pnlGame, BorderLayout.WEST);
      this.add(pnlTime, BorderLayout.EAST);
      this.add(pnlComputerHand, BorderLayout.NORTH);
      this.add(pnlPlayArea, BorderLayout.CENTER);
      this.add(pnlHumanHand, BorderLayout.SOUTH);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setVisible(true);
   }
   
   public void addMessage(int playerCount, int cpuCount)
   {
	   message.append("player passes:" + playerCount);
	   message.append("\n");
	   message.append("cpu passes:" + cpuCount);
	   message.append("\n");
   }
   
   public void setVisible()
   {
      setVisible(true);
   }
   
   //listeners for quit, cannot play and timer start/stop
   public void addQuitListener(ActionListener ev)
   {
      this.quit.addActionListener(ev);
   }
   
   //will be updated to false when player clicks card in hand
   public void addCannotPlayListener(ActionListener ev)
   {
      this.cannotPlay.addActionListener(ev);
   }
   
   public void addButtonListener(ActionListener ev)
   {
      for (int i = 0; i < pnlPlayArea.getComponentCount(); i++)
      {
         ((JButton)pnlPlayArea.getComponent(i)).putClientProperty("key", i);
         ((JButton)pnlPlayArea.getComponent(i)).addActionListener(ev);
      }
   }
}
//END class GameView

//START class GameControl
class GameControl 
{
   private GameModel model;
   private GameView view;
   
   public GameControl()
   {
      model = new GameModel();
      view = new GameView(new ClockTimer());
      model.dealCards();
      model.setTable(view.pnlComputerHand, view.pnlHumanHand, view.pnlPlayArea);
      view.addQuitListener(new QuitListener());
      view.addCannotPlayListener(new CantPlayListener());
      view.setVisible();
   }
   
   public GameControl(GameModel model, GameView view)
   {
      this.model = model;
      this.view = view;
      model.dealCards();
      model.setTable(view.pnlComputerHand, view.pnlHumanHand, view.pnlPlayArea);
      view.addButtonListener(new ButtonListener());
      view.addQuitListener(new QuitListener());
      view.addCannotPlayListener(new CantPlayListener());
      view.setVisible();
   }
   /*
    * If player/computer cannot play, increments the count.
    * calls models cantPlay() method.
   */
   public void setCannotPlayCount(int i)
   {
       model.cantPlay(i);                   
   }
      
   class QuitListener implements ActionListener
   {
      public void actionPerformed(ActionEvent ev)
      {
         System.exit(0);
      }
   }
   
   class CantPlayListener implements ActionListener
   {
      public void actionPerformed(ActionEvent ev)
      {
         model.cantPlay(1);
         model.setHumanPlay(true);
         model.computerPlay();
         model.turns(view.pnlComputerHand, view.pnlHumanHand, view.pnlPlayArea);
         view.pnlPlayArea.removeAll();
         view.pnlHumanHand.removeAll();
         view.pnlComputerHand.removeAll();
         model.setTable(view.pnlComputerHand, view.pnlHumanHand, view.pnlPlayArea);
         view.addMessage(model.humanScore,model.computerScore);
         view.addButtonListener(new ButtonListener());
      }
   }
   
   class ButtonListener implements ActionListener
   {
      public void actionPerformed(ActionEvent ev)
      {
         int cardIndex;
         cardIndex = Integer.parseInt(model.cardSelected.getSelection().getActionCommand());
         
         JButton btn = (JButton) ev.getSource();
         
         Card temp = model.highCardGame.getHand(1).inspectCard(cardIndex);
         if ((Integer) btn.getClientProperty("key") == 0)
         {
            if (model.getDifference(temp, model.getLeftCard()) == 1)
            {
               model.setLeftCard(temp);
               model.playCard(1, cardIndex);
            }
         }
         else if ((Integer) btn.getClientProperty("key") == 1)
         {
            if (model.getDifference(temp, model.getRightCard()) == 1)
            {
               model.setRightCard(temp);
               model.playCard(1, cardIndex);
            }
         }
         
         view.pnlPlayArea.removeAll();
         view.pnlHumanHand.removeAll();
         view.pnlComputerHand.removeAll();
         model.computerPlay(view);
         model.setTable(view.pnlComputerHand, view.pnlHumanHand, view.pnlPlayArea);
         
         view.addButtonListener(new ButtonListener());
      }
   }//  END ButtonListener
}
//END class GameControl

/*****************************************************************************
* BELOW THIS LINE IS EXISTING CODE FROM MODULE 5:
* classes Card, Hand, Deck, GUICard and CardGameFramework
*****************************************************************************/

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

//START class Deck
//Deck class holds all available cards for distribution to hands.
class Deck
{
   public static final int MAX_CARDS = 6 * 56;

   private static Card[] masterPack = new Card[56];
   private Card[] cards;
   private int topCard;

   //START constructors
   //Default constructor
   public Deck()
   {
      allocateMasterPack();
      init(1);
   }

   //Constructor
   public Deck(int numPacks)
   {
      allocateMasterPack();
      init(numPacks);
   }
   
   //Initializes deck with correct number of instantiated cards.
   public void init(int numPacks)
   {
      cards = new Card[numPacks * 56];
      for (int i = 0; i < numPacks; i++)
      {
         System.arraycopy(masterPack, 0, this.cards, i * masterPack.length,
            masterPack.length);
      }
      if ((numPacks * 56) > MAX_CARDS)
         topCard = MAX_CARDS - 1;
      else
         topCard = (numPacks * 56) - 1;
   }
   
   /*
   * shuffle method
   * randomizes indices of existing cards in deck
   */
   public void shuffle()
   {
      Random shuffle = new Random();

      for (int i=0; i < cards.length; i++)
      {
         int randomIndex = i + shuffle.nextInt(cards.length - i);
         Card swap = cards[randomIndex];
         cards[randomIndex] = cards[i];
         cards[i] = swap;
      }
   }

   /*
   * dealCard returns a card while topCard is not negative, otherwise return
   * null
   */
   public Card dealCard()
   {
      if (topCard != -1) //since a card is stored at 0, deck is empty at -1
         return cards[topCard--];
      return null;
   }

   //getTopCard returns topCard integer
   public int getTopCard()
   {
      return topCard;
   }
   /*
   * inspectCard return a card with errorFlag = true if k is out of bounds
   * return card otherwise.
   */
   public Card inspectCard(int k)
   {
      if (k <= topCard)
         return cards[k];
      return new Card('X', Card.Suit.clubs);
   }

   /*
   * allocateMasterPack generates proper card values for the pack
   */
   private static void allocateMasterPack()
   {
      int k = 0; //for deck array number

      if (masterPack[0] != null)
         return;

      for (int i = 0; i < Card.Suit.values().length; i++)
      {
         for (int j = 0; j < Card.cValue.length; j++)
         {
            masterPack[k++] = new Card(Card.cValue[j], Card.Suit.values()[i]);
         }
      }
   }

   public boolean addCard(Card card)
   {
      //make sure that there are not too many instances of the card in the
      //deck if you add it.  Return false if there will be too many.  It should
      //put the card on the top of the deck.

      int count = 0;

      // get number of cards in the deck already
      for (int i = 0; i < cards.length; i++)
      {
         if (cards[i].equals(card))
         {
            count++;
         }
      }

      // check and see if too many
      if (count > 0)
      {
         return false;
      }
      else
      {
         this.cards[topCard++] = card;
         return true;
      }
   }

   public boolean removeCard(Card card)
   {
      //you are looking to remove a specific card from the deck.  Put the
      //current top card into its place.  Be sure the card you need is actually
      //still in the deck, if not return false.
      
      int location = -1;
      
      // get number of cards in the deck already
      for (int i = 0; i < cards.length; i++)
      {
         if (cards[i].equals(card))
         {
            location = i;
            break;
         }
      }
      
      if (location > -1)
      {
         Card temp = this.dealCard();
         this.cards[location] = temp;
         return true;
      }
      else
      {
         return false;
      }
   }

   public void sort()
   {
      //put all of the cards in the deck back into the right order according to
      //their values.  Is there another method somewhere that already does this
      //that you could refer to?

      Card.arraySort(this.cards, this.getNumCards());
   }

   public int getNumCards()
   {
      //return the number of cards remaining in the deck.
      return (topCard + 1);
   }
}
//END class Deck

//START class GUICard
class GUICard
{
 //A 2-D array to store cards representation and point values
 //14 = A thru K + X (X = Joker)
 //4 = suits
 private static Icon[][] iconCards = new ImageIcon[14][4];
 private static Icon iconBack;
 static boolean iconsLoaded = false;

 //generates image icon array from files
 static void loadCardIcons()
 {
    if (iconsLoaded == true)
       return;
    int rows = iconCards.length;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < rows; i++)
    {
       for (Card.Suit s: Card.Suit.values())
       {
          sb.append("images/" + Card.cValue[i] + Character.toUpperCase(s.toString().charAt(0)) + ".gif");
          iconCards[i][s.ordinal()] = new ImageIcon(sb.toString());
          sb.setLength(0);
       }
    }
    iconBack = new ImageIcon("images/" + "BK.gif");
    iconsLoaded = true;
 }

 static public Icon getIcon(Card card)
 {
   loadCardIcons(); // should call method above loadCardIcons
   return iconCards[valueAsInt(card)][suitAsInt(card)];
 }

 static public Icon getBackCardIcon()
 {
   return iconBack;
 }

 private static int suitAsInt(Card card)
 {
   return card.getSuit().ordinal();
 }

 private static int valueAsInt(Card card)
 {
   char cardsValue = card.getValue();
   for(int k = 0; k < 14; k++)
   {
       if(Card.valuRanks[k] == cardsValue)
          return k;
    }
    return 0; //should return an A
 }
 static String turnIntIntoCardValue(int k)
    {
       return String.valueOf(Card.valuRanks[k]);
    }

 // turns 0 - 3 into c, d, h .s
  static String turnIntIntoCardSuit(int j)
    {
       return Card.Suit.values()[j].toString();
    }

}
//END class GUICard

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
