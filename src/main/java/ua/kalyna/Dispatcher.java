package ua.kalyna;

import org.springframework.stereotype.Component;
import ua.kalyna.handler.UserRequestHandler;
import ua.kalyna.model.UserRequest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Dispatcher {

    private final List<UserRequestHandler> handlers;

    /**
     * Pay attention at this constructor
     * Since we have some global handlers,
     * like command /start which can interrupt any conversation flow.
     * These global handlers should go first in the list
     */
    public Dispatcher(List<UserRequestHandler> handlers) {
        this.handlers = handlers
                .stream()
                .sorted(Comparator
                        .comparing(UserRequestHandler::isGlobal)
                        .reversed())
                .collect(Collectors.toList());
    }

    public boolean dispatch(UserRequest userRequest) throws Exception {
        for (UserRequestHandler userRequestHandler : handlers) {
            if(userRequestHandler.isApplicable(userRequest)){
                userRequestHandler.handle(userRequest);
                return true;
            }
        }
        return false;
    }
}

