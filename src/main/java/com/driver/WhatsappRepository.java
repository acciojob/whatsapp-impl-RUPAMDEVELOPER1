package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;
    private Map<String, User> userMap;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMap = new HashMap<String,User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public String createUser(String name, String mobile) throws Exception
    {
        //If the mobile number exists in database, throw "User already exists" exception
        //Otherwise, create the user and return "SUCCESS"
        if (!userMobile.contains(mobile))
        {
            userMobile.add(mobile);
            User user = new User(name, mobile);
            userMap.put(name, user);
            return "SUCCESS";
        }
        else
        {
            throw new Exception("User already exists");
        }
    }
    public Group createGroup(List<User> users)
    {
        //int count = 0;
        if (users.size()>2)
        {
            customGroupCount++;
            String gName = "Group "+String.valueOf(customGroupCount);
            String adName = users.get(0).getName();
            Group grp = new Group(gName, users.size());
            groupUserMap.put(grp, users);
            adminMap.put(grp, users.get(0));
            return grp;
        }
        else if (users.size()==2)
        {
            String gName = users.get(1).getName();
            String adName = users.get(0).getName();
            Group grp = new Group(gName, users.size());
            groupUserMap.put(grp, users);
            adminMap.put(grp, users.get(0));
            return grp;
        }
        return null;         //if users.size()<2
    }
    public int createMessage(String content)
    {
        messageId++;
        Message message = new Message(messageId, content);
        return  messageId;
    }
    public int sendMessage(Message message, User sender, Group group) throws Exception
    {
        if (groupUserMap.containsKey(group))
        {
            List<User> currUsers = groupUserMap.get(group);
            if (currUsers.contains(sender))
            {
                senderMap.put(message, sender);
                if (groupMessageMap.containsKey(group))
                {
                    List<Message> msgList = groupMessageMap.get(group);
                    msgList.add(message);
                    groupMessageMap.put(group, msgList);
                    return msgList.size();
                }
                else
                {
                    List<Message> msgList = new ArrayList<>();
                    msgList.add(message);
                    groupMessageMap.put(group, msgList);
                    return msgList.size();
                }
            }
            else
            {
                throw new Exception("You are not allowed to send message");
            }
        }
        else
        {
            throw new Exception("Group does not exist");
        }
    }

    // 5)
    public String changeAdmin(User approver, User user, Group group) throws Exception
    {
        if (adminMap.containsKey(group))
        {
            User currAdmin = adminMap.get(group);
            if (approver.equals(currAdmin))
            {
                List<User> currUsers = groupUserMap.get(group);
                if (currUsers.contains(user))
                {
                    adminMap.put(group, user);
                    return "SUCCESS";
                }
                else
                {
                    throw new Exception("User is not a participant");
                }
            }
            else
            {
                throw new Exception("Approver does not have rights");
            }
        }
        else
        {
            throw new Exception("Group does not exist");
        }
    }
}

