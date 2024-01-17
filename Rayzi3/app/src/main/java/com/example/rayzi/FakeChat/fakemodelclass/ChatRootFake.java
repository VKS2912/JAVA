package com.example.rayzi.FakeChat.fakemodelclass;

/**
 * Created by Basim on 19-May-21.
 */
public class ChatRootFake {
    private int flag;
    private String message;
    private String image;

    public ChatRootFake(int flag, String message, String image) {
        this.flag = flag;
        this.message = message;
        this.image = image;
    }
//
//    public static List<ChatRootFake> setFakeChates() {
//        List<ChatRootFake> list = new ArrayList<>();
//        list.add(new ChatRootFake(2, "Hii", chatUser.getImage()));
//        list.add(new ChatRootFake(2, "Hello", chatUser.getImage()));
//        list.add(new ChatRootFake(2, "How are you?", chatUser.getImage()));
//        list.add(new ChatRootFake(2, "Have a nice day", chatUser.getImage()));
//        list.add(new ChatRootFake(2, "Lets call", chatUser.getImage()));
//        list.add(new ChatRootFake(2, "Where are you?", chatUser.getImage()));
//        list.add(new ChatRootFake(2, "Give me your number", chatUser.getImage()));
//        list.add(new ChatRootFake(2, "Do you know me ?", chatUser.getImage()));
//
//
//        return list;
//    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }
}
