package dev.avetisyan.egs.bookstore.services;

import dev.avetisyan.egs.bookstore.auth.UserRole;
import dev.avetisyan.egs.bookstore.dtos.request.UserRequestDto;
import dev.avetisyan.egs.bookstore.dtos.request.general.PageCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.UserResponseDto;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorCode;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorDto;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;
import dev.avetisyan.egs.bookstore.entities.UserEntity;
import dev.avetisyan.egs.bookstore.repositories.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends BaseService implements IUserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, ModelMapper mapper) {
        super(mapper);
        this.userRepository = userRepository;
    }

    @Override
    public ResponseDto create(UserRequestDto body) {
        UserEntity user = mapper.map(body, UserEntity.class);
        user.setCreatedDate(new Date(System.currentTimeMillis()));
        user.setRoleId(UserRole.USER.getId());
        // TODO: bcrypt the password 64/4
        user.setPassHash(body.getPassword());

        return tryToSaveUser(user);
    }

    @Override
    public ResponseDto getUsers(Short roleId, PageCriteria pageCriteria) {

        Pageable pageable = PageRequest.of(pageCriteria.getPageIndex(), pageCriteria.getPageSize(),
                Sort.by(Sort.Direction.ASC, "username", "id"));

        Page<UserEntity> userEntities = roleId == null ? userRepository.findAll(pageable) :
                userRepository.findAllByRoleId(roleId, pageable);

        List<UserResponseDto> users = userEntities.get().
                map(e -> mapper.map(e, UserResponseDto.class)).
                collect(Collectors.toCollection(ArrayList::new));

        return ResponseDto.success((Serializable) users, userEntities.getTotalElements());
    }

    private ResponseDto tryToSaveUser(UserEntity user) {
        try {
            UserEntity saved = userRepository.save(user);
            return ResponseDto.success(mapper.map(saved, UserResponseDto.class));

        } catch (DataIntegrityViolationException e) {

            ErrorDto error = null;
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cause = (ConstraintViolationException) e.getCause();
                switch (cause.getConstraintName()) {
                    case "fk_users_role_id_2_roles_id" -> error = new ErrorDto(ErrorCode.ERR_NF, "User role with the specified id not found.");
                    case "unique_email" -> error = new ErrorDto(ErrorCode.ERR_DUP, "User with the specified email already exists.");
                    case "unique_username" -> error = new ErrorDto(ErrorCode.ERR_DUP, "User with the specified username already exists.");
                }
            } else if (e.getCause() instanceof DataException && e.getCause().getCause() != null) {
                error = new ErrorDto(ErrorCode.ERR_LE, e.getCause().getCause().getLocalizedMessage());
            }

            return ResponseDto.error(error == null ? ErrorDto.internalError() : error);
        }
    }
}
