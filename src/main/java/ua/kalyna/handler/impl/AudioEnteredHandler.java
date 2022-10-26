package ua.kalyna.handler.impl;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ua.kalyna.enums.ConversationState;
import ua.kalyna.handler.UserRequestHandler;
import ua.kalyna.helper.KeyboardHelper;
import ua.kalyna.model.UserRequest;
import ua.kalyna.model.UserSession;
import ua.kalyna.nlp.Nlp;
import ua.kalyna.sender.VolunteerHelpBotSender;
import ua.kalyna.service.TelegramService;
import ua.kalyna.service.UserSessionService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Component
public class AudioEnteredHandler extends UserRequestHandler {

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final UserSessionService userSessionService;
    private final VolunteerHelpBotSender botSender;

    public AudioEnteredHandler(TelegramService telegramService, KeyboardHelper keyboardHelper, UserSessionService userSessionService, VolunteerHelpBotSender botSender) {
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.userSessionService = userSessionService;
        this.botSender = botSender;
    }

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isVoiceMessage(userRequest.getUpdate())
                && ConversationState.WAITING_FOR_AUDIO.equals(userRequest.getUserSession().getState());
    }

    @Override
    public void handle(UserRequest userRequest) throws Exception {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildModesMenu();
        telegramService.sendMessage(userRequest.getChatId(),"Аудіо прийнято, чекайте на текст", replyKeyboardMarkup);

        Voice inputVoice = userRequest.getUpdate().getMessage().getVoice();


        URL url = new URL("https://api.telegram.org/bot"+botSender.getBotToken()+"/getFile?file_id="+inputVoice.getFileId());
        BufferedReader in = new BufferedReader(new InputStreamReader( url.openStream()));
        String res = in.readLine();
        JSONObject jresult = new JSONObject(res);
        JSONObject path = jresult.getJSONObject("result");
        String file_path = path.getString("file_path");
        URL downoload = new URL("https://api.telegram.org/file/bot" + botSender.getBotToken() + "/" + file_path);
        FileOutputStream fos = new FileOutputStream("src/main/resources/" + "output2.ogg");
        System.out.println("Start upload");
        ReadableByteChannel rbc = Channels.newChannel(downoload.openStream());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
//        uploadFlag = 0;
        System.out.println("Uploaded!");


        telegramService.sendMessage(userRequest.getChatId(), Nlp.stt("src/main/resources/output2.ogg"), replyKeyboardMarkup);

        UserSession session = userRequest.getUserSession();
//        session.setText(text);
        session.setState(ConversationState.WAITING_FOR_MODE);
        userSessionService.saveSession(userRequest.getChatId(), session);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
