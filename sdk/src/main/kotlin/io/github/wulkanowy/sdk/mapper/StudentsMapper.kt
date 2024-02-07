package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.hebe.register.RegisterDevice
import io.github.wulkanowy.sdk.hebe.register.StudentInfo
import io.github.wulkanowy.sdk.pojo.RegisterEmployee
import io.github.wulkanowy.sdk.pojo.RegisterStudent
import io.github.wulkanowy.sdk.pojo.RegisterSubject
import io.github.wulkanowy.sdk.pojo.RegisterSymbol
import io.github.wulkanowy.sdk.pojo.RegisterUnit
import io.github.wulkanowy.sdk.pojo.RegisterUser
import io.github.wulkanowy.sdk.pojo.Semester
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.sdk.scrapper.register.RegisterEmployee as ScrapperRegisterEmploye
import io.github.wulkanowy.sdk.scrapper.register.RegisterStudent as ScrapperRegisterStudent
import io.github.wulkanowy.sdk.scrapper.register.RegisterSubject as ScrapperRegisterSubject
import io.github.wulkanowy.sdk.scrapper.register.RegisterSymbol as SdkRegisterSymbol
import io.github.wulkanowy.sdk.scrapper.register.RegisterUnit as ScrapperRegisterUnit
import io.github.wulkanowy.sdk.scrapper.register.RegisterUser as ScrapperRegisterUser

internal fun ScrapperRegisterUser.mapUser(): RegisterUser = RegisterUser(
    email = email,
    login = login,
    scrapperBaseUrl = baseUrl,
    loginType = loginType,
    loginMode = Sdk.Mode.SCRAPPER,
    symbols = symbols.map { it.mapSymbol() },
)

internal fun SdkRegisterSymbol.mapSymbol(): RegisterSymbol = RegisterSymbol(
    symbol = symbol,
    userName = userName,
    error = error,
    keyId = null,
    privatePem = null,
    hebeBaseUrl = null,
    schools = schools.map { it.mapUnit() },
)

internal fun ScrapperRegisterUnit.mapUnit(): RegisterUnit = RegisterUnit(
    userLoginId = userLoginId,
    schoolId = schoolId,
    schoolName = schoolName,
    schoolShortName = schoolShortName,
    parentIds = parentIds,
    studentIds = studentIds,
    employeeIds = employeeIds,
    error = error,
    subjects = subjects.map { it.mapSubject() },
)

internal fun ScrapperRegisterSubject.mapSubject(): RegisterSubject {
    return when (this) {
        is ScrapperRegisterStudent -> mapStudent()
        is ScrapperRegisterEmploye -> mapEmployee()
    }
}

internal fun ScrapperRegisterEmploye.mapEmployee(): RegisterEmployee = RegisterEmployee(
    employeeId = employeeId,
    employeeName = employeeName,
)

internal fun ScrapperRegisterStudent.mapStudent(): RegisterStudent = RegisterStudent(
    studentId = studentId,
    studentName = studentName,
    studentSecondName = studentSecondName,
    studentSurname = studentSurname,
    className = className,
    classId = classId,
    isParent = isParent,
    semesters = semesters.mapSemesters(),
    isAuthorized = isAuthorized,
)

fun List<StudentInfo>.mapHebeUser(
    device: RegisterDevice,
): RegisterUser = RegisterUser(
    email = device.userName,
    login = device.userLogin,
    scrapperBaseUrl = null,
    loginType = null,
    loginMode = Sdk.Mode.HEBE,
    symbols = this
        .groupBy { it.topLevelPartition }
        .mapNotNull { (symbol, students) ->
            RegisterSymbol(
                symbol = symbol,
                error = null,
                keyId = device.certificateHash,
                privatePem = device.privatePem,
                hebeBaseUrl = device.restUrl,
                userName = students.firstOrNull()?.login?.displayName ?: return@mapNotNull null,
                schools = students.mapUnit(),
            )
        },
)

private fun List<StudentInfo>.mapUnit(): List<RegisterUnit> {
    return this
        .groupBy { it.unit.symbol }
        .mapNotNull { (schoolId, students) ->
            val firstStudent = students.firstOrNull() ?: return@mapNotNull null
            RegisterUnit(
                userLoginId = firstStudent.login.id,
                schoolId = schoolId,
                schoolName = firstStudent.constituentUnit.name,
                schoolShortName = firstStudent.constituentUnit.short,
                parentIds = listOf(),
                studentIds = listOf(),
                employeeIds = listOf(),
                error = null,
                subjects = students.map { student ->
                    RegisterStudent(
                        studentId = student.pupil.id,
                        studentName = student.pupil.let { pupil -> "${pupil.firstName} ${pupil.surname}" },
                        studentSecondName = student.pupil.secondName,
                        studentSurname = student.pupil.surname,
                        className = student.classDisplay,
                        classId = -1, // todo
                        isParent = student.login.loginRole != "Uczen",
                        isAuthorized = true,
                        semesters = student.periods.map { period ->
                            Semester(
                                diaryId = student.journal.id,
                                kindergartenDiaryId = 0,
                                diaryName = student.classDisplay,
                                schoolYear = period.start.timestamp.toLocalDate().year,
                                semesterId = period.id,
                                semesterNumber = period.number,
                                start = period.start.timestamp.toLocalDate(),
                                end = period.end.timestamp.toLocalDate(),
                                classId = -1, // todo
                                className = student.classDisplay,
                                unitId = student.unit.id, // todo: is needed?
                            )
                        },
                    )
                },
            )
        }
}
