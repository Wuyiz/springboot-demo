package com.example.common.result;

public enum ResultCode {
    SUCCESS(true, 200, "操作成功！"),
    ERROR(false, 10001, "操作失败！"),
    UNAUTHENTICATED(false, 10002, "您还未登录，请登录后再试！"),
    UNAUTHORISED(false, 10003, "您的权限不足！"),
    SERVER_ERROR(false, 99999, "抱歉，系统发生错误，请联系管理员！"),
    SCHEDULE_EXCEPTION(false, 100000, "重复排班异常"),
    PATROL_EXCEPTION(false, 100001, "该用户今日未被排班巡更路线"),
    HANDLER_EXCEPTION(false, 100002, "事件已归档"),
    COMMUNICATION_ADD_EXCEPTION(false, 100003, "融合通信增加联系人错误");

    final boolean status;
    final int code;
    final String message;

    ResultCode(boolean status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
