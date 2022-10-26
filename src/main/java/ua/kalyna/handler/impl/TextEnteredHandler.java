package ua.kalyna.handler.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ua.kalyna.enums.ConversationState;
import ua.kalyna.handler.UserRequestHandler;
import ua.kalyna.helper.KeyboardHelper;
import ua.kalyna.model.UserRequest;
import ua.kalyna.model.UserSession;
import ua.kalyna.nlp.Nlp;
import ua.kalyna.service.TelegramService;
import ua.kalyna.service.UserSessionService;

import java.io.File;
import java.nio.file.NotLinkException;

@Component
public class TextEnteredHandler extends UserRequestHandler {

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final UserSessionService userSessionService;

    public TextEnteredHandler(TelegramService telegramService, KeyboardHelper keyboardHelper, UserSessionService userSessionService) {
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.userSessionService = userSessionService;
    }

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isTextMessage(userRequest.getUpdate())
                && ConversationState.WAITING_FOR_TEXT.equals(userRequest.getUserSession().getState());
    }

    @Override
    public void handle(UserRequest userRequest) throws Exception {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildModesMenu();
        telegramService.sendMessage(userRequest.getChatId(),"Текст прийнято, чекайте на аудіо", replyKeyboardMarkup);

        String text = userRequest.getUpdate().getMessage().getText();
        telegramService.sendVoice(userRequest.getChatId(), new InputFile(new File(Nlp.tts(text))), replyKeyboardMarkup);

        UserSession session = userRequest.getUserSession();
        session.setText(text);
        session.setState(ConversationState.WAITING_FOR_MODE);
        userSessionService.saveSession(userRequest.getChatId(), session);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
