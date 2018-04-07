package service;

import dao.MessageDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public class MessageService {
    private static MessageService messageService = new MessageService();
    private MessageDao messageDao = MessageDao.getMessageDao();
    public static MessageService getMessageService(){
        return messageService;
    }

    public void updateRead(int mid, Handler<AsyncResult<Void>> handler){
        messageDao.updateRead(mid,handler);
    }
}
