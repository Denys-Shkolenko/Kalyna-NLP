package ua.kalyna.helper;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

import static ua.kalyna.constant.Constants.BTN_CANCEL;

/**
 * Helper class, allows to build keyboards for users
 */
@Component
public class KeyboardHelper {

    public ReplyKeyboardMarkup buildModesMenu() {
        List<KeyboardButton> buttons = List.of(
                new KeyboardButton("TTS"),
                new KeyboardButton("STT"));
        KeyboardRow row1 = new KeyboardRow(buttons);

//        KeyboardRow row2 = new KeyboardRow(List.of(new KeyboardButton(BTN_CANCEL)));

        return ReplyKeyboardMarkup.builder()
//                .keyboard(List.of(row1, row2))
                .keyboard(List.of(row1))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    public ReplyKeyboardMarkup buildStartMenu() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("Почати навчання!");

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    public ReplyKeyboardMarkup buildMenuWithCancel() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(BTN_CANCEL);

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }
}
