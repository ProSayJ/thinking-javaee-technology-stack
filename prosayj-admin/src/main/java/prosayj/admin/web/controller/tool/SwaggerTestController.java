package prosayj.admin.web.controller.tool;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import prosayj.framework.common.core.controller.BaseController;
import prosayj.framework.common.core.domain.AjaxResult;
import prosayj.framework.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * swagger 用户测试方法
 *
 * @author ProSayJ
 * Api()：用在请求的类上，表示对类的说明，也代表了这个类是swagger2的资源 <br>
 * tags：说明该类的作用，参数是个数组，可以填多个。
 * value="该参数没什么意义，在UI界面上不显示，所以不用配置"
 * description = "用户基本信息操作"<hr>
 */
@Api(tags = {"用于测试swagger常用注解的一些使用方法"})
@RestController
@RequestMapping("/test/user")
public class SwaggerTestController extends BaseController {
    private final static Map<Integer, UserEntity> users = new LinkedHashMap<Integer, UserEntity>();

    static {
        users.put(1, new UserEntity(1, "admin", "admin123", "15888888888"));
        users.put(2, new UserEntity(2, "zhangsan", "admin123", "15666666666"));
    }

    /**
     * ApiOperation()：用于方法，表示一个http请求访问该方法的操作 <br>
     * tags = {"说明该方法的作用，参数是个数组，在这里建议不使用这个参数，会使界面看上去有点乱，常用的是：value和notes。可以填多个", "11", "22", "33"}
     */
    @ApiOperation(
            value = "00-方法的用途和作用-(获取用户列表)",
            notes = "方法的注意事项和一些备注")
    @ApiResponse(code = 0, message = "成功")
    @GetMapping("/list")
    public AjaxResult userList() {
        List<UserEntity> userList = new ArrayList<>(users.values());
        return AjaxResult.success(userList);
    }

    /**
     * ApiImplicitParam：用于方法，表示单独的请求参数
     * name="参数名"
     * value="参数说明"
     * dataType="数据类型"
     * paramType="query" 表示参数放在哪里
     * · header 请求参数的获取：@RequestHeader
     * · query   请求参数的获取：@RequestParam
     * · path（用于restful接口） 请求参数的获取：@PathVariable
     * · body（不常用）
     * · form（不常用）
     * defaultValue="参数的默认值"
     * required="true" 表示参数是否必须传
     */
    @ApiOperation("01-获取用户详细")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "int", required = true, paramType = "path")
    @GetMapping("/{userId}")
    public AjaxResult getUser(@PathVariable Integer userId) {
        if (!users.isEmpty() && users.containsKey(userId)) {
            return AjaxResult.success(users.get(userId));
        } else {
            return AjaxResult.error("用户不存在");
        }
    }

    /**
     * ApiParam()：用于方法，参数，字段说明 表示对参数的要求和说明
     * name="参数名称"
     * value="参数的简要说明"
     * defaultValue="参数默认值"
     * required="true" 表示属性是否必填，默认为false
     */
    @ApiOperation("02-新增用户")
    @ApiImplicitParam(name = "userEntity", value = "新增用户信息", dataType = "UserEntity")
    @PostMapping("/save")
    public AjaxResult save(@ApiParam(name = "userEntity", value = "新增用户信息", required = true) UserEntity user) {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId())) {
            return AjaxResult.error("用户ID不能为空");
        }
        return AjaxResult.success(users.put(user.getUserId(), user));
    }

    @ApiOperation("更新用户")
    @ApiImplicitParam(name = "userEntity", value = "新增用户信息", dataType = "UserEntity")
    @PutMapping("/update")
    public AjaxResult update(UserEntity user) {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId())) {
            return AjaxResult.error("用户ID不能为空");
        }
        if (users.isEmpty() || !users.containsKey(user.getUserId())) {
            return AjaxResult.error("用户不存在");
        }
        users.remove(user.getUserId());
        return AjaxResult.success(users.put(user.getUserId(), user));
    }

    @ApiOperation("删除用户信息")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "int", paramType = "path")
    @DeleteMapping("/{userId}")
    public AjaxResult delete(@PathVariable Integer userId) {
        if (!users.isEmpty() && users.containsKey(userId)) {
            users.remove(userId);
            return AjaxResult.success();
        } else {
            return AjaxResult.error("用户不存在");
        }
    }
}

/**
 * ApiModel()：用于响应实体类上，用于说明实体作用
 * description="描述实体的作用"
 */
@ApiModel("用户实体")
class UserEntity {
    @ApiModelProperty(value = "用户ID", example = "123123")
    private Integer userId;

    @ApiModelProperty(value = "用户名称", required = true, example = "zhangsan")
    private String username;

    @ApiModelProperty(value = "用户密码", required = true, example = "abc123456")
    private String password;

    @ApiModelProperty(value = "用户手机", required = true, example = "15665656565")
    private String mobile;

    public UserEntity() {

    }

    public UserEntity(Integer userId, String username, String password, String mobile) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.mobile = mobile;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
