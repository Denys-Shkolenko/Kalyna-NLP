package ua.kalyna.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import ua.kalyna.model.UserRequest;

public abstract class UserRequestHandler {

    public abstract boolean isApplicable(UserRequest request);
    public abstract void handle(UserRequest dispatchRequest) throws Exception;
    public abstract boolean isGlobal();

    public boolean isCommand(Update update, String command) {
        return update.hasMessage() && update.getMessage().isCommand()
                && update.getMessage().getText().equals(command);
    }

    public boolean isTextMessage(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    public boolean isTextMessage(Update update, String text) {
        return update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals(text);
    }

    public boolean isVoiceMessage(Update update) {
        return update.hasMessage() && update.getMessage().hasVoice();
    }
}