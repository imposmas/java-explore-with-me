package ru.practicum.ewm.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.exceptions.ConflictException;
import ru.practicum.ewm.common.exceptions.NotFoundException;
import ru.practicum.ewm.common.util.PaginationUtils;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Create a new user.
     */
    @Override
    public UserDto create(NewUserRequest dto) {
        log.debug("ADMIN: Creating user with email={}", dto.getEmail());

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email must be unique");
        }

        User user = userMapper.toEntity(dto);
        UserDto result = userMapper.toDto(userRepository.save(user));

        log.info("ADMIN: User created id={}", result.getId());
        return result;
    }

    /**
     * Delete a user by ID.
     */
    @Override
    public void delete(Long id) {
        log.debug("ADMIN: Attempting to delete user {}", id);

        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with id=" + id + " was not found");
        }

        userRepository.deleteById(id);
        log.info("ADMIN: User {} deleted", id);
    }

    /**
     * Get users by IDs or paginated list if IDs not provided.
     */
    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        log.debug("ADMIN: Fetching users (ids={}, from={}, size={})", ids, from, size);

        List<User> users;

        if (ids == null || ids.isEmpty()) {
            Pageable pageable = PaginationUtils.toPageable(from, size);
            users = userRepository.findAll(pageable).stream().toList();
            log.debug("ADMIN: Fetched {} users via pagination", users.size());
        } else {
            users = userRepository.findAllById(ids);
            log.debug("ADMIN: Fetched {} users by IDs", users.size());
        }

        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}