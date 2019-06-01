// This program keeps track of the state of Hangman which is played
// by a user. However, instead of having a target word in mind at the 
// beginning of the game, it delays picking a word until it  
// has to.
import java.util.*;
public class HangmanManager {
   
   private Set<String> words; 
   private SortedSet<Character> guesses;
   private int guessesLeft;
   private String pattern;
   private Map<String, Set<String>> patternHolder; 
   
   // pre: user provided length is greater than one and maximum number of guess provided
   // by user is greater than 0 (throws IllegalArgumentException if not)
   // post: accepts word length and max number of guesses from the user to initialize the 
   // state of the game. returns a "pattern" of dashes, where the number of dashes is the
   // length of the words, which is provided by the player. creates set of words from 
   // dictionary file with words of given length, and eliminates any duplicates added. 
   public HangmanManager (Collection<String> dictionary, int length, int max) {
      if (length < 1 || max < 0) {
         throw new IllegalArgumentException();
      }
      guesses = new TreeSet<Character>();
      guessesLeft = max;
      words = new TreeSet<String>();
      pattern = "";
      pattern += "-";
      for (int i = 0; i < length - 1; i++) {
         pattern += " -";
      }
      Iterator<String> it = dictionary.iterator();
      while (it.hasNext()) {
         String word = it.next();  
         if (word.length() == length && !words.contains(word)) {
            words.add(word);
         }
      } 
   }
   
   // returns current set of words being considered by hangman
   // manager
   public Set <String> words() {   
      return words;
   }
   
   // returns how many guesses user has left. a guess is only used
   // if the program decides not to incorporate the guessed word
   // into the game
   public int guessesLeft() {
      return guessesLeft;
   }
   
   // returns set of letters which have been guessed by the user
   // returns both letters that have been used by program and
   // those which the program has not decided to use. 
   public SortedSet<Character> guesses() {
      return guesses;
   }
   
   // pre: set of words should not be empty (if false, throws 
   // IllegalArgumentException)
   // post:  returns current pattern at current state of game
   public String pattern() {
      if (words.isEmpty()) {
         throw new IllegalArgumentException();
      }
      return pattern;
   }
   
   // pre: player has at least 1 guess left or set of words is not 
   // empty (throws IllegalStateException if not true). also, set of
   // words should be empty if the character being guessed was guessed 
   // previously (throws IllegalArgumentException if untrue)
   // post: takes the next guess given by user, records it and decides  
   // what set of words to use going forward. Returns number of occurrences of
   // guessed letter in the new pattern, and updates how many guesses 
   // user has left
   public int record (char guess) {
      if (words.isEmpty() || guessesLeft < 1) {
         throw new IllegalStateException();
      }
      if (!words.isEmpty() && guesses.contains(guess)) {
         throw new IllegalArgumentException();
      }
      patternHolder = new TreeMap<String, Set<String>>();
      guesses.add(guess);
      String topPattern = pattern;
      updatePatternEntries(pattern,topPattern, guess);  
      topPattern = updateTopPattern(topPattern);  
      words = patternHolder.get(topPattern);
      if(!topPattern.contains(String.valueOf(guess))) {
         guessesLeft = guessesLeft - 1;
      }
      pattern = topPattern;
      int counter = 0;
      for (int i = 0; i < topPattern.length(); i++) {
         if(topPattern.charAt(i) == guess) {
            counter++;
         }
      }
      return counter;
   }
   
   // accepts word from current set of words and assigns the word a pattern. returns
   // pattern for the word 
   private String updatePattern (String currentWord, String topPattern, char guess) {
      pattern = "";
      for(int i = 0; i < currentWord.length() * 2; i += 2) {
         if(Character.isLetter(topPattern.charAt(i)) && topPattern.charAt(i) != guess) {
            pattern += topPattern.charAt(i);
         } else if(currentWord.charAt(i / 2) == guess) {
            pattern += guess;
         } else {
            pattern += "-";     
         }
         pattern += " ";
      }  
      return pattern.substring(0, pattern.length() - 1);        
   }
   
   // checks to see which pattern has the most words in the
   // current game. returns that pattern
   private String updateTopPattern(String topPattern) {
      int topWordCount = 0;
      for (String pattern : patternHolder.keySet()) {
         if (patternHolder.get(pattern).size() > topWordCount) {
            topWordCount = patternHolder.get(pattern).size();
            topPattern = pattern;
         }
      }
      return topPattern;
   }
   
   // for the current set of words in the current game, seperates words by their assigned 
   // patterns and groups those with the same pattern together
   private void updatePatternEntries(String pattern, String topPattern, char guess) {
      Iterator<String> it = words.iterator();
      while (it.hasNext()) { 
         String currentWord = it.next();
         pattern = updatePattern(currentWord, topPattern, guess);
         if(!patternHolder.containsKey(pattern)) { 
            Set<String> words = new TreeSet<String>(); 
            patternHolder.put(pattern, words); 
            words.add(currentWord); 
         } else {
            Set<String> words = patternHolder.get(pattern);
            words.add(currentWord);
         }    
      }
   }
}
