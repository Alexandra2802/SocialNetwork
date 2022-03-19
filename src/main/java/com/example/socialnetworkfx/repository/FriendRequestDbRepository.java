package com.example.socialnetworkfx.repository;

import com.example.socialnetworkfx.domain.FriendRequest;
import com.example.socialnetworkfx.domain.FriendRequestStatus;
import com.example.socialnetworkfx.domain.User;
import com.example.socialnetworkfx.domain.validators.RepoException;
import com.example.socialnetworkfx.domain.validators.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class FriendRequestDbRepository implements Repository<Long, FriendRequest> {
    private String url;
    private String username;
    private String password;
    private Validator<FriendRequest> validator;
    private UserDbRepository userDbRepo;

    public FriendRequestDbRepository(String url,
                                     String username,
                                     String password,
                                     Validator<FriendRequest> validator,
                                     UserDbRepository userDbRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.userDbRepo=userDbRepo;
    }

    @Override
    public FriendRequest findOne(Long aLong) {
        String sql="SELECT * from friendrequests where friendrequests.id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
        )
        {
            statement.setLong(1,aLong);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                Long from_id =resultSet.getLong("from_user_id");
                Long to_id =resultSet.getLong("to_user_id");
                FriendRequestStatus status= FriendRequestStatus.valueOf(resultSet.getString("status"));
                User from=userDbRepo.findOne(from_id);
                User to=userDbRepo.findOne(to_id);
                if(from==null)
                    throw new RepoException("Utilizatorul care trimite cererea nu exista");
                if(to==null)
                    throw new RepoException("Utilizatorul care primeste cererea nu exista");
                FriendRequest fr=new FriendRequest(from,to,status);
                fr.setId(id);
                return fr;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        Set<FriendRequest> friendRequests = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendrequests");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                FriendRequest fr=findOne(id);
                fr.setId(id);
                friendRequests.add(fr);
            }
            return friendRequests;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendRequests;
    }

    @Override
    public FriendRequest save(FriendRequest entity) {
        String sql = "insert into friendrequests(from_user_id,to_user_id,status) values(?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if(entity.getFrom()!=null && entity.getTo()!=null)
            {
                ps.setLong(1, entity.getFrom().getId());
                ps.setLong(2, entity.getTo().getId());
                ps.setString(3,entity.getStatus().toString());
                ps.executeUpdate();
            }
            else throw new RepoException("Utilizatorul nu exista");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getSize() {
        int nr=0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT count(*) from friendrequests");
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
    public void remove(FriendRequest entity) {
        String sql="delete from friendrequests where id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1,entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(FriendRequest entity, FriendRequest newEntity) {
        String sql="update friendrequests set from_user_id=? ,to_user_id=?, status=? where id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1,newEntity.getFrom().getId());
            ps.setLong(2,newEntity.getTo().getId());
            ps.setString(3,newEntity.getStatus().toString());
            ps.setLong(4,entity.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

