package com.example.socialnetworkfx.domain.validators;

import com.example.socialnetworkfx.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship>{
    @Override
    public void validate(Friendship f) throws ValidationException {
        if(f.getuser1ID()==f.getuser2ID())
            throw new ValidationException("Utilizatorii trebuie sa fie diferiti");

        if(f.getDate().charAt(2)!='/' || f.getDate().charAt(5)!='/')
            throw new ValidationException("Data trebuie sa aiba urmatorul format: DD/MM/YYYY!");

    }

}
