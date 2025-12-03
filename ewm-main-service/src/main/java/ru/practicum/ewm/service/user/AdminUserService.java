package ru.practicum.ewm.service.user;

import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;

import java.util.List;

public interface AdminUserService {

    UserDto create(NewUserRequest dto);

    void delete(Long id);

    List<UserDto> getUsers(List<Long> ids, int from, int size);
}
