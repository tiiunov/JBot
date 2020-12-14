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
    private TaskPointer taskPointer = TaskPointer.FREE;
    private final ReplyKeyboardMarkup REPLY_KEYBOARD_MARKUP = InitKeyboard();

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage, REPLY_KEYBOARD_MARKUP);
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
            switch (message.getText()) {
                case Commands.HELP:
                    sendMsg(message, RequestAnswers.HELP);
                    break;
                case Commands.WEATHER:
                    taskPointer = TaskPointer.WEATHER;
                    sendMsg(message, RequestAnswers.WEATHER);
                    break;
                case Commands.CLOTHING_ADVICE:
                    sendMsg(message, RequestAnswers.CLOVING_ADVICE);
                    taskPointer = TaskPointer.ADVICE;
                    break;
                default:
                    if (taskPointer != TaskPointer.FREE) {
                        sendMsg(message, performTask(message.getText(), taskPointer));
                        taskPointer = TaskPointer.FREE;
                    } else
                        sendMsg(message, RequestAnswers.EMPTY_TASK);
                    break;
            }
        }
    }

    private String performTask(String message, TaskPointer task) {
        String result;
        try {
            if (task == TaskPointer.WEATHER)
                result = Weather.getAnswer(message);
            else
                result = ClothingAdvice.getAdvice(message);
        } catch (IOException e) {
            result = RequestAnswers.DEFAULT;
        }
        return result;
    }

    public void setButtons(SendMessage sendMessage, ReplyKeyboardMarkup REPLY_KEYBOARD_MARKUP) {
        sendMessage.setReplyMarkup(REPLY_KEYBOARD_MARKUP);
    }

    public ReplyKeyboardMarkup InitKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();

        firstRow.add(new KeyboardButton(Commands.WEATHER));
        firstRow.add(new KeyboardButton(Commands.CLOTHING_ADVICE));
        secondRow.add(new KeyboardButton(Commands.HELP));

        keyboardRowList.add(firstRow);
        keyboardRowList.add(secondRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        return replyKeyboardMarkup;
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
