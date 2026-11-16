package chatapp;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class SearchMessagesByUser implements Iterator<Message> {
    private final List<Message> messages;
    private final User userToSearchWith;
    private int index = 0;

    public SearchMessagesByUser(List<Message> messages, User userToSearchWith) {
        this.messages = messages;
        this.userToSearchWith = userToSearchWith;
    }

    @Override
    public boolean hasNext() {
        while (index < messages.size()) {
            if (involvesUser(messages.get(index))) {
                return true;
            }
            index++;
        }
        return false;
    }

    @Override
    public Message next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more messages with " + userToSearchWith.getName());
        }
        Message message = messages.get(index);
        index++;
        return message;
    }

    private boolean involvesUser(Message message) {
        return message.getSender().equals(userToSearchWith)
                || message.getRecipients().contains(userToSearchWith);
    }
}
