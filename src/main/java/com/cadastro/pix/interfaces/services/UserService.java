package com.cadastro.pix.interfaces.services;

import com.cadastro.pix.domain.user.User;
import com.cadastro.pix.dto.resp.RespDTO;
import com.cadastro.pix.dto.user.UserDTO;
import com.cadastro.pix.dto.user.UserListDTO;
import com.cadastro.pix.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserService {
    public RespDTO createUser(User user);

    public RespDTO findAllUsers();

    public RespDTO findUserById(UUID id);

    public RespDTO updateUser(UUID id, User user);

    public RespDTO deleteUser(UUID id);
}
