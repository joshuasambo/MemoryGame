
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

public class MemoryCardController{

    //ability
    @FXML AnchorPane abilitiesAnchorPane;
    @FXML HBox hBoxRemainContainer;
    @FXML Label currentAbilityDisplay;
    @FXML Label inefficientWarningLabel;
    @FXML Label abilityDescription;

    String currentAbility;
    boolean currentlyAbilityInUse = false;
    Random random = new Random();

    String[] abilitiesDescription = {"Gains extra 2 - 5 attempts", "Multiply scores by 2 or 3",
                                     "Reveals chosen card's pair for 2 seconds", "Reveals 2 - 5 random cards for 2 seconds"};

    ArrayList<Label> abilityRemainContainer = new ArrayList<>();
    HashMap<String, Integer> abilityAndPurchased = new HashMap<>(Map.of(
            "Glass", 0,
            "Key", 0,
            "Cash", 0,
            "Bolts", 0
    ));

    @FXML
    public void abilitiesButton(){
        mainAnchorPane.setVisible(false);
        abilitiesAnchorPane.setVisible(true);
        abilitiesAnchorPane.setMouseTransparent(false);

    }

    boolean oneAbilityUse = true;

    @FXML
    public void abilityUsePick(ActionEvent event){

        if(!oneAbilityUse){
            inefficientWarningLabel.setText(currentAbility + " is currently in use");
            FadeTransition ft = new FadeTransition(Duration.seconds(4), inefficientWarningLabel);
            ft.setFromValue(1.0);
            ft.setToValue(0);
            ft.setCycleCount(1);
            ft.play();
            return;
        }
        Button button = (Button)event.getSource();

        if(button.getId().equalsIgnoreCase(currentAbility)){// reset the current use code
            currentlyAbilityInUse = false;
            currentAbilityDisplay.setText("None");
            abilityDescription.setText("");
            currentAbility = "";
            dotingCount();
            return;
        }

        String buttonId = button.getId().substring(0,1).toUpperCase() + button.getId().substring(1);

        if(abilityAndPurchased.get(buttonId) <= 0){
            // *** INEFFICIENT ABILITY ***
            inefficientWarningLabel.setText(buttonId + " inefficient");
            FadeTransition ft = new FadeTransition(Duration.seconds(4), inefficientWarningLabel);
            ft.setFromValue(1.0);
            ft.setToValue(0);
            ft.setCycleCount(1);
            ft.play();
        }else{
            // *** ABILITY IN USE ***
            currentAbility = buttonId;
            currentlyAbilityInUse = true;
            currentAbilityDisplay.setText(buttonId);
            loadingTimeline.stop();
            loadingTimeline.getKeyFrames().clear();
            startButton.setText(buttonId);

            switch(buttonId){
                case"Bolts" -> abilityDescription.setText(abilitiesDescription[0]);
                case"Cash" -> abilityDescription.setText(abilitiesDescription[1]);
                case"Key" -> abilityDescription.setText(abilitiesDescription[2]);
                case"Glass" -> abilityDescription.setText(abilitiesDescription[3]);

            }

        }

    }




    //shop anchor pane
    @FXML AnchorPane shopAnchorPane;
    @FXML Label shopTotalScores;
    @FXML Label moneyAmount;
    @FXML Label highestScoreDisplay;



    int highestScore = 0;
    int saveTotalScores = 0;

    @FXML
    public void convertScores(){
        int scoresDisplay = Integer.parseInt(shopTotalScores.getText());
        if(scoresDisplay == 1) return;

        int moneyTemp = scoresDisplay / 2 + (Integer.parseInt(moneyAmount.getText()));

        moneyAmount.setText(String.valueOf(moneyTemp));
        shopTotalScores.setText(String.valueOf(scoresDisplay % 2));
        saveTotalScores = scoresDisplay % 2;

    }

    @FXML
    public void purchaseButton(ActionEvent event){
        if(Integer.parseInt(moneyAmount.getText()) <= 0) return;

        int money = Integer.parseInt(moneyAmount.getText());
        int price = Integer.parseInt(((Button)event.getSource()).getText());

        if(money < price) return;

        money -= price;
        moneyAmount.setText(String.valueOf(money));

        Button purchased = ((Button)event.getSource());
        String abilityName = purchased.getUserData().toString();

        int num = abilityAndPurchased.get(abilityName) + 1;// increase ability purchased
        abilityAndPurchased.replace(abilityName, num);

        Label counterRemainLabel = abilityRemainContainerFinder(abilityName);
        assert counterRemainLabel != null;
        counterRemainLabel.setText(String.valueOf(abilityAndPurchased.get(abilityName)));

    }

    private Label abilityRemainContainerFinder(String abilityName){

        for(Label remainLabel : abilityRemainContainer){
            if(remainLabel.getId().equalsIgnoreCase(abilityName)) return remainLabel;
        }

        return null;
    }

    @FXML
    public void shopButton(){
        mainAnchorPane.setVisible(false);
        shopAnchorPane.setVisible(true);
        shopAnchorPane.setMouseTransparent(false);
    }

    @FXML
    public void backButton(ActionEvent event){
        Node backButton = (Node)event.getSource();

        while(backButton != null && !(backButton instanceof AnchorPane)){
            backButton = backButton.getParent();
        }

        if(backButton == shopAnchorPane){
            mainAnchorPane.setVisible(true);
            shopAnchorPane.setVisible(false);
            shopAnchorPane.setMouseTransparent(true);

        }else if(backButton == abilitiesAnchorPane){
            mainAnchorPane.setVisible(true);
            abilitiesAnchorPane.setVisible(false);
            abilitiesAnchorPane.setMouseTransparent(true);
        }



    }




    // main anchor pane
    @FXML AnchorPane mainAnchorPane;
    @FXML Button startButton;
    @FXML GridPane grid;
    @FXML Label attemptCounter;
    @FXML Label scoreCounter;

    private int attemptLeft = 0;
    private int scoreMultiplier = 1;
    private int scores = 0;

    boolean isPlaying;
    private final Timeline loadingTimeline = new Timeline();
    private int dotCount = 0;

    Button firstCard;
    boolean canClickCard = true;

    private final ArrayList<Button> buttons = new ArrayList<>();

    ArrayList<String> card = new ArrayList<>(Arrays.asList("🍎","🍍","🥝","🍇","🌻","🌸","🍀","🍁",
                                                           "🍎","🍍","🥝","🍇","🌻","🌸","🍀","🍁"));

    @FXML
    public void initialize(){

        for(Node button : grid.getChildren()){
            buttons.add((Button)button);
        }

        for(Node label : hBoxRemainContainer.getChildren()){
            abilityRemainContainer.add((Label)label);
        }



        shuffle();

    }

    boolean keyAbility = false;

    @FXML
    public void cardButton(ActionEvent event){
        if(!canClickCard || event.getSource() == firstCard || attemptLeft <= 0) return;

        Button currentClickedCard = ((Button)event.getSource());
        String cardDataCurrent = currentClickedCard.getUserData().toString();
        

        if(keyAbility && firstCard == null){
            // ***** KEY ABILITY *****
            
            String cardData = currentClickedCard.getUserData().toString();
            Button secondCard = null;

            for(Button card : buttons){
                if(card.getUserData().toString().equalsIgnoreCase(cardData)){
                    secondCard = card;
                    break;
                }
            }
            assert secondCard != null;
            currentClickedCard.getStyleClass().add("ability");
            secondCard.getStyleClass().add("ability");

            System.out.println(currentClickedCard.getUserData());
            System.out.println(secondCard.getUserData());

            Button finalSecondCard = secondCard;
            KeyFrame revealTwoCards = new KeyFrame(Duration.seconds(2.5), e ->{
                currentClickedCard.getStyleClass().remove("ability");
                finalSecondCard.getStyleClass().remove("ability");
            });

            loadingTimeline.getKeyFrames().add(revealTwoCards);
            loadingTimeline.setCycleCount(1);
            loadingTimeline.play();

            keyAbility = false;

        }else if(firstCard == null){
            //first card revealed
            currentClickedCard.setText((String)currentClickedCard.getUserData());
            firstCard = currentClickedCard;

        }else{
            if(firstCard.getText().equals(cardDataCurrent)){
                //Matched
                currentClickedCard.setText((String)currentClickedCard.getUserData());
                
                firstCard.setDisable(true);
                currentClickedCard.setDisable(true);

                firstCard.getStyleClass().add("matched");
                currentClickedCard.getStyleClass().add("matched");

                firstCard = null;

                attemptLeft--;
                showAttemptLeft();

                scores += scoreDetermine(currentClickedCard.getText()) * scoreMultiplier;
                scoreCounter.setText(String.valueOf(scores));

            }else{
                //Unmatched
                currentClickedCard.setText((String)currentClickedCard.getUserData());
                
                canClickCard = false;
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e ->{
                    firstCard.setText("");
                    currentClickedCard.setText("");
                    firstCard = null;
                    canClickCard = true;
                });

                pause.play();
                attemptLeft--;
                showAttemptLeft();

            }

            if(attemptLeft == 0){
                loadingTimeline.stop();

                loadingTimeline.getKeyFrames().clear();
                scoreCounter.getStyleClass().remove("abilityCash");
                startButton.getStyleClass().remove("ability");
                startButton.setText("Game over");

                KeyFrame showAll = new KeyFrame(Duration.seconds(1.8),e ->{
                    for(Button button : buttons){
                        button.setText(button.getUserData().toString());
                    }

                });

                KeyFrame keyFrame = new KeyFrame(Duration.seconds(2.8), e ->{
                    startButton.setText("Play again?");
                    isPlaying = false;
                });

                loadingTimeline.setCycleCount(1);
                loadingTimeline.getKeyFrames().addAll(showAll, keyFrame);
                loadingTimeline.play();
            }
        }



    }

    public void shuffle(){
        Collections.shuffle(card);

        int index = 0;
        for(Button button : buttons){
            button.setUserData(card.get(index));
            button.setText(card.get(index));
            index++;

        }

    }

    @FXML
    public void play(ActionEvent event){

        if(isPlaying && !currentlyAbilityInUse){
            System.out.println("Still running");
            return;
        }else if(currentlyAbilityInUse){

            Button abilityActionButton = (Button)event.getSource();
            abilityActionButton.getStyleClass().add("ability");
            String abilityName = abilityActionButton.getText();

            switch(abilityName){
                case "Bolts" -> {
                    attemptLeft += random.nextInt(2, 6);
                    showAttemptLeft();

                }
                case "Cash" -> {
                    scoreCounter.getStyleClass().add("abilityCash");
                    scoreMultiplier = random.nextInt(2, 4);

                }
                case "Key" -> keyAbility = true;
                case "Glass" -> {

                    loadingTimeline.stop();
                    loadingTimeline.getKeyFrames().clear();

                    Collections.shuffle(buttons);
                    int num = random.nextInt(4, 7);
                    KeyFrame[] keyFrames = new KeyFrame[num];
                    int index = 0;

                    for(Button button : buttons){
                        if(index >= num) break;

                        if(button == null) break;

                        if(button.isDisabled()) continue;
                        button.setText(button.getUserData().toString());
                        keyFrames[index] = new KeyFrame(Duration.seconds(5),
                                new KeyValue(button.textFillProperty(), Color.web("#386641")));

                        index++;

                    }

                    playAllKeyFrames(keyFrames);

                }

            }

            Label counterRemainLabel = abilityRemainContainerFinder(abilityName);
            assert counterRemainLabel != null;
            int count = Integer.parseInt(counterRemainLabel.getText()) - 1;
            abilityAndPurchased.put(abilityName, count);

            counterRemainLabel.setText(String.valueOf(count));
            oneAbilityUse = false;
            currentlyAbilityInUse = false;

            return;

        }else{
            if(startButton.getText().equals("Play again?")){
                saveTotalScores += scores;

                highestScore = Math.max(highestScore, saveTotalScores);
                highestScoreDisplay.setText(String.valueOf(highestScore));

                shopTotalScores.setText(String.valueOf(saveTotalScores));
                oneAbilityUse = true;

                currentAbilityDisplay.setText("None");
                abilityDescription.setText("");

                loadingTimeline.stop();
                loadingTimeline.getKeyFrames().clear();

            }
        }

        scores = 0;
        scoreCounter.setText(String.valueOf(scores));

        attemptLeft = 8;
        isPlaying = true;
        showAttemptLeft();

        shuffle();
        buttons.forEach((b) ->{
            b .setText(" ");
            b.setDisable(false);
            b.getStyleClass().remove("matched");
        });

        dotingCount();

    }



    public void playAllKeyFrames(KeyFrame[] keyFrames){
        canClickCard = false;

        for(KeyFrame keyFrame : keyFrames){
            if(keyFrame == null) break;

            loadingTimeline.getKeyFrames().add(keyFrame);
        }

        KeyFrame turnOnClickCard = new KeyFrame(Duration.seconds(5), e ->{
            canClickCard = true;

            for(Button button : buttons){
                if(button == null) break;

                if(!button.getText().equals(" ") && !button.isDisabled()){
                    button.setText(" ");
                    button.getStyleClass().add("resetColor");
                }
            }

        });

        loadingTimeline.getKeyFrames().add(turnOnClickCard);
        loadingTimeline.setCycleCount(1);
        loadingTimeline.play();

    }


    public void dotingCount(){
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), e ->{
            dotCount = dotCount % 4;
            startButton.setText("•" + "•".repeat(dotCount));
            dotCount++;
        });

        loadingTimeline.getKeyFrames().add(keyFrame);
        loadingTimeline.setCycleCount(Timeline.INDEFINITE);
        loadingTimeline.play();
    }

    public void showAttemptLeft(){
        attemptCounter.setText(String.valueOf(attemptLeft));
    }

    private int scoreDetermine(String card){

        return switch(card){
            case "🍎" -> 20;
            case "🍍" -> 23;
            case "🥝" -> 34;
            case "🍇" -> 42;
            case "🌻" -> 51;
            case "🌸" -> 63;
            case "🍀" -> 72;
            case "🍁" -> 81;
            default -> 0;
        };
    }


}
