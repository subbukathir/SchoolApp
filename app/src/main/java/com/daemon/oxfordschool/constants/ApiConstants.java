package com.daemon.oxfordschool.constants;

/**
 * Created by daemonsoft on 28/1/16.
 */
public class ApiConstants
{
    public static String SERVER_URL = "http://125.17.102.136/OxfordSchool/";
    //public static String SERVER_URL = "http://192.168.2.103/OxfordSchool/";
    public static String LOGIN_URL = SERVER_URL + "User.php";
    public static String STUDENT_LIST= SERVER_URL + "StudentList.php";
    public static String UPLOADIMAGE_URL= SERVER_URL + "SaveProfileImage.php";
    public static String VIEWPROFILE_URL= SERVER_URL + "ViewProfile.php";

    public static String CLASSLIST_URL=SERVER_URL + "ClassList.php";
    public static String SECTIONLIST_URL=SERVER_URL +"GetSectionList.php";
    public static String ALLSUBJECTLIST_URL=SERVER_URL + "GetAllSubjects.php";
    public static String SUBJECTLIST_URL=SERVER_URL + "GetSubjectsList.php";
    public static String EVENTS_LIST_URL=SERVER_URL + "EventsList.php";
    public static String EXAM_TYPE_LIST_URL=SERVER_URL + "ExamTypeList.php";
    public static String EXAM_LIST_URL=SERVER_URL + "ExamList.php";
    public static String EXAM_RESULT_URL=SERVER_URL + "ExamResultByStudentId.php";
    public static String EXAM_RESULT_URL_STAFF=SERVER_URL + "ExamResult.php";
    public static String HOMEWORK_LIST_URL=SERVER_URL + "HomeWorkList.php";
    public static String HOMEWORK_LIST_STAFF_URL=SERVER_URL + "HomeWorkListStaff.php";
    public static String ATTENDANCE_URL=SERVER_URL + "Attendance.php";
    public static String ATTENDANCE_BY_STUDENT_URL=SERVER_URL + "AttendanceByStudentId.php";
    public static String TIME_TABLE_URL=SERVER_URL + "TimeTable.php";
    public static String ADD_ATTENDANCE=SERVER_URL + "UpdateAttendance.php";
    public static String ADD_HOMEWORK_URL=SERVER_URL + "AddHomeWork.php";
    public static String ADD_DIARY_NOTES_URL=SERVER_URL + "AddDiaryNotes.php";
    public static String DIARY_NOTES_LIST_URL=SERVER_URL + "DiaryNotesList.php";
    public static String DIARY_NOTES_LIST_STAFF_URL=SERVER_URL + "DiaryNotesList_Staff.php";
    public static String ADD_MARKS_URL=SERVER_URL + "AddMarks.php";
    public static String ADD_EVENT_URL=SERVER_URL + "AddEvent.php";
    public static String ADD_SUBJECT_URL=SERVER_URL + "AddSubject.php";
    public static String TERM_LIST_URL=SERVER_URL + "FeesTypeList.php";
    public static String PAYMENT_DETAIL_URL=SERVER_URL + "GetPaymentDetail.php";
    public static String PAYMENT_DETAIL_STUDENT_URL=SERVER_URL + "GetPaymentDetailByStudentId.php";
    public static String CCE_EXAM_REPORT_URL=SERVER_URL + "CCE_ExamReport.php";
    public static String STUDENT_PROFILE_URL=SERVER_URL + "ViewStudentProfile.php";
    public static String DEVICE_REG_URL=SERVER_URL + "device_register.php";
    public static String MASS_NOTIFICATION_URL=SERVER_URL + "Notification.php";
    public static String HOLIDAYS_URL=SERVER_URL + "Holidays.php";
    public static String DISCIPLINE_REPORT_URL=SERVER_URL + "DisciplineReport.php";
    public static String ADD_CLASS_URL=SERVER_URL + "AddClass.php";
    public static String ADD_SECTION_URL=SERVER_URL + "AddSection.php";
    public static String ASSIGN_SECTION_URL=SERVER_URL + "AddSectionClass.php";
    public static String ALL_SECTION_URL=SERVER_URL + "SectionList.php";
    public static String ADD_EXAM_URL=SERVER_URL + "AddExam.php";

    public static String SUCCESS_CODE="0";

    public static String STUDENT="1";
    public static String STAFF="2";
    public static String PARENT="3";
    public static String ADMIN="4";

    public static int PAID=1;
    public static int UNPAID=0;
    public static int PARTIALLY_PAID=2;

}
