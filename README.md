### 勤怠管理アプリケーション


## ◯追加機能 : シフト表の閲覧,管理機能
    管理者が作成したシフトを閲覧することができます。
    未ログイン時は全員分のシフトを、ログイン時は自分が入っている日のシフトを確認できます。
    また、名前/月でのフィルタリングができます。

## ◯主な追加ファイル : 
    ・dto/ScheduleServlet
    ・model/CalendarLogic, MyCalendar
    ・dao/ScheduleDAO
    ・controller/ScheduleServlet
    ・jsp/schedule_list

## ◯テーブル : 
    ・Users
        user情報を管理 : username, password, role, enabled,
    ・Attendance
        勤怠記録を管理 : userid, checkintime, checkouttime,
    ・Schedule
        シフト日程を管理 : userid,scheduledate,

### ◯使用方法
  ## 1. データベース,テーブルを作成
    CREATE DATABASE managementdb;

    CREATE TABLE Users(
    username text PRIMARY KEY,
    password text,
    role text,
    enabled BOOLEAN
    );
    CREATE TABLE Attendance(
    userId text,
    checkInTime timestamp,
    checkOutTime timestamp,
    FOREIGN KEY(userId) REFERENCES Users(username)
    on update cascade
    );
    CREATE TABLE Schedule(
    userId text,
    ScheduleDate timestamp,
    FOREIGN KEY(userId) REFERENCES Users(username)
    on update cascade
    );

  ## 2.初期データを追加(usersは必須、その他は任意)
    INSERT INTO users(username,password,role,enabled)
    VALUES('O森','morimori','employee',true);
    INSERT INTO users(username,password,role,enabled)
    VALUES('T橋','hashihashi','employee',true);
    INSERT INTO users(username,password,role,enabled)
    VALUES('管理者','admin','admin',true);

    INSERT INTO attendance(userid,checkintime,checkouttime)
    VALUES('O森','2025-09-02 17:00:00', '2025-09-02 21:00:00');
    INSERT INTO attendance(userid,checkintime,checkouttime)
    VALUES('T橋','2025-09-04 17:00:00', '2025-09-04 21:00:00');
    INSERT INTO attendance(userid,checkintime,checkouttime)
    VALUES('T橋','2025-09-07 17:00:00', '2025-09-07 21:00:00');
    INSERT INTO attendance(userid,checkintime,checkouttime)
    VALUES('O森','2025-09-08 17:00:00', '2025-09-08 21:00:00');

    INSERT INTO schedule(userid,scheduledate)
    VALUES('T橋','2025-09-07 17:00:00');
    INSERT INTO schedule(userid,scheduledate)
    VALUES('O森','2025-09-08 17:00:00');
    INSERT INTO schedule(userid,scheduledate)
    VALUES('O森','2025-09-14 17:00:00');
    INSERT INTO schedule(userid,scheduledate)
    VALUES('T橋','2025-09-15 17:00:00');

  ## 3.login.jspから起動、ログイン
    ・管理者としてログイン
        ユーザーID : 管理者
        パスワード : admin
    ・従業員としてログイン
        1.
        ユーザーID : O森
        パスワード : morimori
        2.
        ユーザーID : T橋
        パスワード : hashihashi
