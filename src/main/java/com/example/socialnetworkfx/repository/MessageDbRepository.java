package com.example.socialnetworkfx.repository;

import com.example.socialnetworkfx.domain.Message;
import com.example.socialnetworkfx.domain.User;
import com.example.socialnetworkfx.domain.validators.Validator;

import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.sql.Types.NULL;

public class MessageDbRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Validator<Message> validator;
    private UserDbRepository userDbRepo;

    public MessageDbRepository(String url,
                               String username,
                               String password,
                               Validator<Message> validator,
                               UserDbRepository userDbRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.userDbRepo = userDbRepo;
    }

    public Message createMessageFromDbRow(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String text = resultSet.getString("text");
        Long from_id=resultSet.getLong("from_user_id");
        User from=userDbRepo.findOne(from_id);
        Long to_id=resultSet.getLong("to_user_id");
        User to=userDbRepo.findOne(to_id);
        List<User> toList=new ArrayList<User>();
        toList.add(to);
        Long replyingToId = resultSet.getLong("replying_to");
        Message replyingTo=this.findOne(replyingToId);
        String stringDate = resultSet.getString("date");
        String stringTime = resultSet.getString("time");
        LocalDateTime dateTime = LocalDateTime.now();
        String[] parsedDate=stringDate.split("-");
        dateTime=dateTime.withYear(Integer.parseInt(parsedDate[0]));
        dateTime=dateTime.withMonth(Integer.parseInt(parsedDate[1]));
        dateTime=dateTime.withDayOfMonth(Integer.parseInt(parsedDate[2]));
        String[] parsedTime=stringTime.split(":");
        dateTime=dateTime.withHour(Integer.parseInt(parsedTime[0]));
        dateTime=dateTime.withMinute(Integer.parseInt(parsedTime[1]));
        dateTime=dateTime.withSecond(Integer.parseInt(parsedTime[2]));
        Message m= new Message(from,toList,text,dateTime,replyingTo);
        m.setId(id);
        return m;
    }


    @Override
    public Message findOne(Long aLong) {
        String sql="SELECT * from messages where messages.id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
        )
        {
            statement.setLong(1,aLong);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
                return createMessageFromDbRow(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next())
                messages.add(createMessageFromDbRow(resultSet));

            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Message save(Message entity) {
        String sql = "insert into messages (text, date,from_user_id,to_user_id,replying_to,time )" +
                " values (?,?,?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for(User u: userDbRepo.findAll())
            {
                if(entity.getTo().contains(u))
                {
                    ps.setString(1, entity.getText());

                    String stringDate=entity.getDate().getYear()+"-"+entity.getDate().getMonth().getValue()+"-"+
                            entity.getDate().getDayOfMonth();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
                    java.util.Date date = sdf.parse(stringDate);
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                    ps.setDate(2, sqlDate);
                    ps.setLong(3, entity.getFrom().getId());
                    ps.setLong(4,u.getId());
                    if(entity.getReplyingTo()!=null)
                        ps.setLong(5,entity.getReplyingTo().getId());
                    else ps.setNull(5,NULL);
                    String stringTime=entity.getDate().getHour()+":"+entity.getDate().getMinute()+
                            ":"+entity.getDate().getSecond();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    java.util.Date d1 =(java.util.Date)format.parse(stringTime);
                    java.sql.Time time = new java.sql.Time(d1.getTime());
                    ps.setTime(6,time);
                    ps.executeUpdate();
                }
            }

        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getSize() {
        int nr=0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT count(*) from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while(resultSet.next())
                nr=resultSet.getInt(1);
            return nr;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nr;
    }

    @Override
    public void remove(Message entity) {

    }

    @Override
    public void update(Message entity, Message newEntity) {

    }

    public Long findBeginningOfConversation(Long u1_id,Long u2_id)
    //returns id of first message in the conversation between u1 and u2 or
    //null if they never had a conversation
    {
        String sql="SELECT * from messages where replying_to=null and (from=? and to=? or from=? and to=?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
        )
        {
            ps.setLong(1,u1_id);
            ps.setLong(2,u2_id);
            ps.setLong(3,u2_id);
            ps.setLong(4,u1_id);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next())
            {
                return resultSet.getLong("id");
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message findReply(Message start)
    {
        for(Message reply:findAll())
            if(reply.getTo().contains(start.getFrom()) && start.getTo().contains(reply.getFrom()))
                return reply;
        return null;
    }

    public List<Message> findConversation(Long u1_id, Long u2_id)
    {
        String sql="SELECT * from messages where (from_user_id=? and to_user_id=?) or (from_user_id=? and to_user_id=?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
        )
        {
            ps.setLong(1,u1_id);
            ps.setLong(2,u2_id);
            ps.setLong(3,u2_id);
            ps.setLong(4,u1_id);
            ResultSet resultSet = ps.executeQuery();
            List<Message> conversation=new ArrayList<Message>();
            while(resultSet.next())
            {
                Message m=createMessageFromDbRow(resultSet);

                conversation.add(createMessageFromDbRow(resultSet));
            }
            return conversation.stream()
                    .sorted(Comparator.comparing(Message :: getDate))
                    .collect(Collectors.toList());

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
