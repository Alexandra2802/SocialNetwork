package com.example.socialnetworkfx.repository;

import com.example.socialnetworkfx.domain.FriendRequest;
import com.example.socialnetworkfx.domain.FriendRequestStatus;
import com.example.socialnetworkfx.domain.Friendship;
import com.example.socialnetworkfx.domain.validators.RepoException;
import com.example.socialnetworkfx.domain.validators.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDbRepository implements Repository<Long, Friendship> {
    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;
    private FriendRequestDbRepository friendRequestDbRepo;


    public FriendshipDbRepository(String url,
                                  String username,
                                  String password,
                                  Validator<Friendship> validator,
                                  FriendRequestDbRepository friendRequestDbRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.friendRequestDbRepo=friendRequestDbRepo;
    }

    @Override
    public Friendship findOne(Long aLong) {
        String sql="SELECT * from friendships where id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
        )
        {
            statement.setLong(1,aLong);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                Long user1_id=resultSet.getLong("user1_id");
                Long user2_id=resultSet.getLong("user2_id");
                String date=resultSet.getString("date");
                Friendship f=new Friendship(user1_id,user2_id,date);
                f.setId(id);
                return f;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long user1_id = resultSet.getLong("user1_id");
                Long user2_id = resultSet.getLong("user2_id");
                String date=resultSet.getString("date");
                Friendship f=new Friendship(user1_id,user2_id,date);
                f.setId(id);
                friendships.add(f);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Friendship save(Friendship entity) {
        String sql = "insert into friendships (user1_id, user2_id ) values (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            boolean friendRequestAdded=false;
            for(FriendRequest fr:friendRequestDbRepo.findAll())
                if( (fr.getFrom().getId()==entity.getuser1ID() && fr.getTo().getId()== entity.getuser2ID()) ||
                        (fr.getFrom().getId()==entity.getuser2ID() && fr.getTo().getId()== entity.getuser1ID()) &&
                                fr.getStatus().equals(FriendRequestStatus.APPROVED))
                {
                    ps.setLong(1, entity.getuser1ID());
                    ps.setLong(2, entity.getuser2ID());
                    ps.setString(3, entity.getDate());

                    ps.executeUpdate();
                    friendRequestAdded=true;
                }
            if(!friendRequestAdded)
                throw new RepoException("Cererea de prietenie nu exista sau nu a fost aprobata");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getSize() {
        int nr=0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT count(*) from friendships");
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
    public void remove(Friendship entity) {
        String sql="delete from friendships where id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1,entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Friendship entity, Friendship newEntity) {
        String sql="update friendships set user1_id=? ,user2_id=?, date=?  where id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1,newEntity.getuser1ID());
            ps.setLong(2,newEntity.getuser2ID());
            ps.setString(3, newEntity.getDate());
            ps.setLong(4,entity.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
