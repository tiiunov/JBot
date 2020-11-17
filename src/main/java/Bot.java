import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Bot extends TelegramLongPollingBot {
    private String botName = "sdfhgd_bot";
    private String botToken = readBotToken();
    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String readBotToken(){
        File file = new File("../../../config/token.txt");
        Scanner in = null;
        {
            try {
                in = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String token = in.nextLine();
        return token;
    }



    @Override
    public void onUpdateReceived(Update update) {
        Model model = new Model();
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            if (Commands.HELP.equals(message.getText())) {
                sendMsg(message, Request_answers.HELP);
            }
            else if (Commands.WEATHER.equals(message.getText())){
                sendMsg(message, Request_answers.WEATHER);
            }
            else
                try {
                    sendMsg(message, Weather.getWeather(message.getText(), model));
                }   catch (IOException e) {
                    sendMsg(message, Request_answers.DEFAULT);
                }
        }

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
