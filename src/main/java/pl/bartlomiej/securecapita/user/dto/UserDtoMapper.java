package pl.bartlomiej.securecapita.user.dto;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import pl.bartlomiej.securecapita.user.User;

@Component
public class UserDtoMapper {
    public static UserReadDto map(User user) {
        UserReadDto userReadDto = new UserReadDto();
        BeanUtils.copyProperties(user, userReadDto);
        return userReadDto;
    }

    public static User map(UserReadDto userReadDto) {
        User user = new User();
        BeanUtils.copyProperties(userReadDto, user);
        return user;
    }

    public static User map(UserCreateDto userCreateDto) {
        User user = new User();
        BeanUtils.copyProperties(userCreateDto, user);
        return user;
    }
}
