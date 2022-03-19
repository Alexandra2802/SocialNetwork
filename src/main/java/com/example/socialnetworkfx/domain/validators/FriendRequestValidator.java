package com.example.socialnetworkfx.domain.validators;


import com.example.socialnetworkfx.domain.FriendRequest;
import com.example.socialnetworkfx.domain.FriendRequestStatus;

public class FriendRequestValidator implements Validator<FriendRequest>{
    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        if(entity.getTo().equals(entity.getFrom()))
            throw new ValidationException("Utilizatorii trebuie sa fie diferiti!");
        if(!entity.getStatus().equals(FriendRequestStatus.PENDING) &&
                !entity.getStatus().equals(FriendRequestStatus.APPROVED) &&
                !entity.getStatus().equals(FriendRequestStatus.REJECTED))
            throw new ValidationException("Status trebuie sa fie PENDING, APPROVED sau REJECTED");
    }
}

