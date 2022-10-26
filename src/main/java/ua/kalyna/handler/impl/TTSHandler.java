package ua.kalyna.handler.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ua.kalyna.enums.ConversationState;
import ua.kalyna.handler.UserRequestHandler;
import ua.kalyna.helper.KeyboardHelper;
import ua.kalyna.model.UserRequest;
import ua.kalyna.model.UserSession;
import ua.kalyna.service.TelegramService;
import ua.kalyna.service.UserSessionService;

@Component
public class TTSHandler extends UserRequestHandler {

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final UserSessionService userSessionService;

    public TTSHandler(TelegramService telegramService, KeyboardHelper keyboardHelper, UserSessionService userSessionService) {
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.userSessionService = userSessionService;
    }

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isTextMessage(userRequest.getUpdate(), "TTS")
                && ConversationState.WAITING_FOR_MODE.equals(userRequest.getUserSession().getState());
    }

    @Override
    public void handle(UserRequest userRequest) {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildMenuWithCancel();
        telegramService.sendMessage(userRequest.getChatId(),
                "Введіть текст:",
                replyKeyboardMarkup);

        UserSession userSession = userRequest.getUserSession();
        userSession.setState(ConversationState.WAITING_FOR_TEXT);
        userSessionService.saveSession(userSession.getChatId(), userSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

}
