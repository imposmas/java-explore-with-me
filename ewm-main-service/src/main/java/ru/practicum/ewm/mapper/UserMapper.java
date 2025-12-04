package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.User;

@Component
public class UserMapper {

    public User toEntity(NewUserRequest dto) {
        return User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public UserShortDto toShort(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}