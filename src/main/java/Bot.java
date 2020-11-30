import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bot extends TelegramLongPollingBot {
    private final String botName = "sdfhgd_bot";
    private final String botToken = readBotToken();
    private Integer task_pointer = 0;

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String readBotToken(){
        File file = new File("config/token.txt");
        Scanner in = null;
        {
            try {
                in = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        assert in != null;
        return in.nextLine();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            if (Commands.HELP.equals(message.getText())) {
                sendMsg(message, Request_answers.HELP);
            }
            else if (Commands.WEATHER.equals(message.getText())){
                task_pointer = 1;
                sendMsg(message, Request_answers.WEATHER);
            }
            else if (Commands.CLOVING_ADVICE.equals(message.getText()) && (task_pointer != 1)){
                sendMsg(message, Request_answers.CLOVING_ADVICE);
                task_pointer = 2;
            }
            else if (task_pointer == 1){
                try {
                    sendMsg(message, Weather.getWeather(message.getText(), new Model()));
                    task_pointer = 0;
                }   catch (IOException e) {
                    sendMsg(message, Request_answers.DEFAULT);
                    task_pointer = 0;
                }
            }
            else if (task_pointer == 2){
                try {
                    sendMsg(message, Clothing_advice.getAdvice(message.getText()));
                    task_pointer = 0;
                } catch (IOException e) {
                    sendMsg(message, Request_answers.DEFAULT);
                    task_pointer = 0;
                }
            }
            else {
                sendMsg(message, Request_answers.EMPTY_TASK);
            }
        }
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();

        firstRow.add(new KeyboardButton(Commands.WEATHER));
        firstRow.add(new KeyboardButton(Commands.CLOVING_ADVICE));
        secondRow.add(new KeyboardButton(Commands.HELP));

        keyboardRowList.add(firstRow);
        keyboardRowList.add(secondRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}