package ua.kalyna.handler.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ua.kalyna.enums.ConversationState;
import ua.kalyna.handler.UserRequestHandler;
import ua.kalyna.helper.KeyboardHelper;
import ua.kalyna.model.UserRequest;
import ua.kalyna.model.UserSession;
import ua.kalyna.service.TelegramService;
import ua.kalyna.service.UserSessionService;

@Component
public class StartCommandHandler extends UserRequestHandler {

    private final static String command = "/start";

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final UserSessionService userSessionService;

    public StartCommandHandler(TelegramService telegramService, KeyboardHelper keyboardHelper, UserSessionService userSessionService) {
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.userSessionService = userSessionService;
    }

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isCommand(userRequest.getUpdate(), command);
    }

    @Override
    public void handle(UserRequest userRequest) {
        ReplyKeyboard replyKeyboard = keyboardHelper.buildModesMenu();
        telegramService.sendMessage(userRequest.getChatId(),
                "\uD83D\uDC4BПривіт! За допомогою цього чат-бота ви зможете вивчити українську мову!",
                replyKeyboard);
        telegramService.sendMessage(userRequest.getChatId(),
                "Обирайте з меню нижче ⤵️");

        UserSession userSession = userRequest.getUserSession();
        userSession.setState(ConversationState.WAITING_FOR_MODE);
        userSessionService.saveSession(userSession.getChatId(), userSession);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
