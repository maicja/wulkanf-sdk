package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.hebe.Hebe
import io.github.wulkanowy.sdk.hebe.models.MessageUser
import io.github.wulkanowy.sdk.mapper.mapAttendance
import io.github.wulkanowy.sdk.mapper.mapAttendanceSummary
import io.github.wulkanowy.sdk.mapper.mapCompletedLessons
import io.github.wulkanowy.sdk.mapper.mapConferences
import io.github.wulkanowy.sdk.mapper.mapDevices
import io.github.wulkanowy.sdk.mapper.mapDirectorInformation
import io.github.wulkanowy.sdk.mapper.mapExams
import io.github.wulkanowy.sdk.mapper.mapGradePointsStatistics
import io.github.wulkanowy.sdk.mapper.mapGradeStatistics
import io.github.wulkanowy.sdk.mapper.mapGrades
import io.github.wulkanowy.sdk.mapper.mapGradesSemesterStatistics
import io.github.wulkanowy.sdk.mapper.mapHebeUser
import io.github.wulkanowy.sdk.mapper.mapHomework
import io.github.wulkanowy.sdk.mapper.mapLastAnnouncements
import io.github.wulkanowy.sdk.mapper.mapLuckyNumbers
import io.github.wulkanowy.sdk.mapper.mapMailboxes
import io.github.wulkanowy.sdk.mapper.mapMenu
import io.github.wulkanowy.sdk.mapper.mapMessages
import io.github.wulkanowy.sdk.mapper.mapNotes
import io.github.wulkanowy.sdk.mapper.mapPhoto
import io.github.wulkanowy.sdk.mapper.mapRecipients
import io.github.wulkanowy.sdk.mapper.mapSchool
import io.github.wulkanowy.sdk.mapper.mapScrapperMessage
import io.github.wulkanowy.sdk.mapper.mapSemesters
import io.github.wulkanowy.sdk.mapper.mapStudent
import io.github.wulkanowy.sdk.mapper.mapSubjects
import io.github.wulkanowy.sdk.mapper.mapTeachers
import io.github.wulkanowy.sdk.mapper.mapTimetableFull
import io.github.wulkanowy.sdk.mapper.mapToScrapperAbsent
import io.github.wulkanowy.sdk.mapper.mapToUnits
import io.github.wulkanowy.sdk.mapper.mapToken
import io.github.wulkanowy.sdk.mapper.mapUser
import io.github.wulkanowy.sdk.pojo.Absent
import io.github.wulkanowy.sdk.pojo.Attendance
import io.github.wulkanowy.sdk.pojo.AttendanceSummary
import io.github.wulkanowy.sdk.pojo.CompletedLesson
import io.github.wulkanowy.sdk.pojo.Conference
import io.github.wulkanowy.sdk.pojo.Device
import io.github.wulkanowy.sdk.pojo.DirectorInformation
import io.github.wulkanowy.sdk.pojo.Exam
import io.github.wulkanowy.sdk.pojo.Folder
import io.github.wulkanowy.sdk.pojo.GovernmentUnit
import io.github.wulkanowy.sdk.pojo.GradePointsStatistics
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSemester
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSubject
import io.github.wulkanowy.sdk.pojo.Grades
import io.github.wulkanowy.sdk.pojo.Homework
import io.github.wulkanowy.sdk.pojo.LastAnnouncement
import io.github.wulkanowy.sdk.pojo.LuckyNumber
import io.github.wulkanowy.sdk.pojo.Mailbox
import io.github.wulkanowy.sdk.pojo.Menu
import io.github.wulkanowy.sdk.pojo.Message
import io.github.wulkanowy.sdk.pojo.MessageDetails
import io.github.wulkanowy.sdk.pojo.MessageReplayDetails
import io.github.wulkanowy.sdk.pojo.Note
import io.github.wulkanowy.sdk.pojo.Recipient
import io.github.wulkanowy.sdk.pojo.RegisterStudent
import io.github.wulkanowy.sdk.pojo.RegisterUser
import io.github.wulkanowy.sdk.pojo.School
import io.github.wulkanowy.sdk.pojo.Semester
import io.github.wulkanowy.sdk.pojo.StudentInfo
import io.github.wulkanowy.sdk.pojo.StudentPhoto
import io.github.wulkanowy.sdk.pojo.Subject
import io.github.wulkanowy.sdk.pojo.Teacher
import io.github.wulkanowy.sdk.pojo.Timetable
import io.github.wulkanowy.sdk.pojo.Token
import io.github.wulkanowy.sdk.scrapper.EvaluateHandler
import io.github.wulkanowy.sdk.scrapper.Scrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import java.net.CookieManager
import java.time.LocalDate
import java.time.ZoneId

class Sdk {

    enum class Mode {
        HEBE,
        SCRAPPER,
        HYBRID,
    }

    enum class ScrapperLoginType {
        AUTO,
        STANDARD,
        ADFS,
        ADFSCards,
        ADFSLight,
        ADFSLightScoped,
        ADFSLightCufs,
    }

    private val scrapper = Scrapper()

    private val hebe = Hebe()

    private val registerTimeZone = ZoneId.of("Europe/Warsaw")

    var mode = Mode.HYBRID

    var mobileBaseUrl = ""
        set(value) {
            field = value
            hebe.baseUrl = value
        }

    var keyId = ""
        set(value) {
            field = value
            hebe.keyId = value
        }

    var privatePem = ""
        set(value) {
            field = value
            hebe.privatePem = privatePem
        }

    var scrapperBaseUrl = ""
        set(value) {
            field = value
            scrapper.baseUrl = value
        }

    var domainSuffix: String = ""
        set(value) {
            field = value
            scrapper.domainSuffix = value
        }

    var isEduOne = false
        set(value) {
            field = value
            scrapper.isEduOne = value
        }

    var email = ""
        set(value) {
            field = value
            scrapper.email = value
        }

    var password = ""
        set(value) {
            field = value
            scrapper.password = value
        }

    var schoolSymbol = ""
        set(value) {
            field = value
            scrapper.schoolId = value
            hebe.schoolId = value
        }

    var classId = 0
        set(value) {
            field = value
            scrapper.classId = value
        }

    var studentId = 0
        set(value) {
            field = value
            scrapper.studentId = value
            hebe.pupilId = value
        }

    var diaryId = 0
        set(value) {
            field = value
            scrapper.diaryId = value
        }

    var unitId = 0
        set(value) {
            field = value
            scrapper.unitId = value
        }

    var kindergartenDiaryId = 0
        set(value) {
            field = value
            scrapper.kindergartenDiaryId = value
        }

    var schoolYear = 0
        set(value) {
            field = value
            scrapper.schoolYear = value
        }

    var symbol = ""
        set(value) {
            field = value
            scrapper.symbol = value
        }

    var loginType = ScrapperLoginType.AUTO
        set(value) {
            field = value
            scrapper.loginType = Scrapper.LoginType.valueOf(value.name)
        }

    var logLevel = HttpLoggingInterceptor.Level.BASIC
        set(value) {
            field = value
            hebe.logLevel = value
            scrapper.logLevel = value
        }

    var userAgentTemplate = ""
        set(value) {
            field = value
            scrapper.userAgentTemplate = value
        }

    val userAgent: String
        get() = scrapper.userAgent

    var androidVersion = "7.0"
        set(value) {
            field = value
            scrapper.androidVersion = value
        }

    var buildTag = "SM-G950F Build/NRD90M"
        set(value) {
            field = value
            hebe.deviceModel = value
            scrapper.buildTag = value
        }

    var endpointsMapping
        get() = scrapper.endpointsMapping
        set(value) {
            scrapper.endpointsMapping = value
        }

    var vTokenMapping
        get() = scrapper.vTokenMapping
        set(value) {
            scrapper.vTokenMapping = value
        }

    var vHeaders
        get() = scrapper.vHeaders
        set(value) {
            scrapper.vHeaders = value
        }

    var responseMapping
        get() = scrapper.responseMapping
        set(value) {
            scrapper.responseMapping = value
        }

    var vParamsEvaluation: suspend () -> EvaluateHandler
        get() = scrapper.vParamsEvaluation
        set(value) {
            scrapper.vParamsEvaluation = value
        }

    var emptyCookieJarInterceptor: Boolean = false
        set(value) {
            field = value
            scrapper.emptyCookieJarInterceptor = value
        }

    private val interceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf()

    fun setSimpleHttpLogger(logger: (String) -> Unit) {
        logLevel = HttpLoggingInterceptor.Level.NONE
        val interceptor = HttpLoggingInterceptor {
            logger(it)
        }.setLevel(HttpLoggingInterceptor.Level.BASIC)
        addInterceptor(interceptor)
    }

    fun addInterceptor(interceptor: Interceptor, network: Boolean = false) {
        scrapper.addInterceptor(interceptor, network)
        hebe.addInterceptor(interceptor, network)
        interceptors.add(interceptor to network)
    }

    fun setAdditionalCookieManager(cookieManager: CookieManager) {
        scrapper.setAdditionalCookieManager(cookieManager)
    }

    @JvmOverloads
    fun switchDiary(diaryId: Int, kindergartenDiaryId: Int, schoolYear: Int, unitId: Int = 0): Sdk = also {
        it.diaryId = diaryId
        it.kindergartenDiaryId = kindergartenDiaryId
        it.schoolYear = schoolYear
        it.unitId = unitId
    }

    suspend fun isSymbolNotExist(symbol: String): Boolean = withContext(Dispatchers.IO) {
        scrapper.isSymbolNotExist(symbol)
    }

    suspend fun getPasswordResetCaptchaCode(registerBaseUrl: String, symbol: String) = withContext(Dispatchers.IO) {
        scrapper.getPasswordResetCaptcha(registerBaseUrl, symbol)
    }

    suspend fun sendPasswordResetRequest(registerBaseUrl: String, symbol: String, email: String, captchaCode: String) = withContext(Dispatchers.IO) {
        scrapper.sendPasswordResetRequest(registerBaseUrl, symbol, email, captchaCode)
    }

    suspend fun getUserSubjectsFromScrapper(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        symbol: String = "Default",
    ): RegisterUser = getUserSubjectsFromScrapper(
        email = email,
        password = password,
        scrapperBaseUrl = scrapperBaseUrl,
        domainSuffix = domainSuffix,
        symbol = symbol,
    )

    suspend fun getUserSubjectsFromScrapper(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        domainSuffix: String,
        symbol: String,
    ): RegisterUser = withContext(Dispatchers.IO) {
        scrapper.let {
            it.baseUrl = scrapperBaseUrl
            it.domainSuffix = domainSuffix
            it.email = email
            it.password = password
            it.symbol = symbol
            it.getUserSubjects().mapUser()
        }
    }

    suspend fun getStudentsFromHebe(
        token: String,
        pin: String,
        symbol: String,
        firebaseToken: String? = null,
    ): RegisterUser {
        val registerDevice = hebe.register(
            firebaseToken = firebaseToken,
            token = token,
            pin = pin,
            symbol = symbol,
        )
        return hebe
            .getStudents(registerDevice.restUrl)
            .mapHebeUser(registerDevice)
    }

    suspend fun getStudentsHybrid(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        startSymbol: String = "Default",
        firebaseToken: String? = null,
    ): RegisterUser = withContext(Dispatchers.IO) {
        val scrapperUser = getUserSubjectsFromScrapper(email, password, scrapperBaseUrl, startSymbol)
        scrapperUser.symbols
            .mapNotNull { symbol ->
                val school = symbol.schools.firstOrNull {
                    it.subjects.filterIsInstance<RegisterStudent>().isNotEmpty()
                } ?: return@mapNotNull null
                val student = school.subjects
                    .firstOrNull() as? RegisterStudent ?: return@mapNotNull null
                scrapper.also {
                    it.symbol = symbol.symbol
                    it.schoolId = school.schoolId
                    it.studentId = student.studentId
                    it.diaryId = -1
                    it.classId = student.classId
                    it.loginType = Scrapper.LoginType.valueOf(scrapperUser.loginType!!.name)
                }
            }

        val token = scrapper.getToken()
        val hebeUser = getStudentsFromHebe(
            token = token.token,
            pin = token.pin,
            symbol = token.symbol,
            firebaseToken = firebaseToken,
        )

        hebeUser.copy(
            loginMode = Mode.HYBRID,
            login = scrapperUser.login,
            email = scrapperUser.email,
            loginType = Scrapper.LoginType.valueOf(scrapperUser.loginType!!.name),
            scrapperBaseUrl = scrapperUser.scrapperBaseUrl,
        )
    }

    suspend fun authorizePermission(pesel: String): Boolean = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.authorizePermission(pesel)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getCurrentStudent(): RegisterStudent = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER, Mode.HYBRID -> scrapper.getCurrentStudent().mapStudent()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getSemesters(
        pin: String = "",
        symbol: String = "",
        token: String = "",
    ): List<Semester> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSemesters().mapSemesters()
            Mode.HEBE -> {
                val registerDevice = hebe.register(
                    firebaseToken = null,
                    token = token,
                    pin = pin,
                    symbol = symbol,
                )

                hebe.getPeriods(registerDevice.restUrl).mapSemesters()
            }
        }
    }

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate): List<Attendance> = withContext(Dispatchers.IO) {
        when (mode) {

            //Mode.SCRAPPER -> scrapper.getAttendance(startDate, endDate).mapAttendance()
            //Mode.HYBRID, Mode.HEBE -> hebe.getCompletedLessons(studentId, startDate, endDate).mapAttendance()

            Mode.SCRAPPER -> scrapper.getAttendance(startDate, endDate).mapAttendance()
            Mode.HYBRID -> scrapper.getAttendance(startDate, endDate).mapAttendance()
            Mode.HEBE -> hebe.getCompletedLessons(studentId, startDate, endDate).mapAttendance()
        }
    }

    suspend fun getAttendanceSummary(subjectId: Int? = -1, startDate: LocalDate, endDate: LocalDate): List<AttendanceSummary> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getAttendanceSummary(subjectId).mapAttendanceSummary()
            Mode.HYBRID -> scrapper.getAttendanceSummary(subjectId).mapAttendanceSummary()
            Mode.HEBE ->
                hebe
                    .getAttendanceSummaryForWholeYear(
                        pupilId = studentId,
                        startDate = startDate,
                        endDate = endDate,
                        subjectId = subjectId ?: -1,
                    ).mapAttendanceSummary()
        }
    }

    suspend fun excuseForAbsence(absents: List<Absent>, content: String? = null): Boolean = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.excuseForAbsence(absents.mapToScrapperAbsent(), content)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getSubjects(periodId: Int): List<Subject> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getSubjects().mapSubjects()
            Mode.HYBRID, Mode.HEBE -> hebe.getSubjects(studentId, periodId).mapSubjects()
        }
    }

    suspend fun getExams(start: LocalDate, end: LocalDate): List<Exam> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getExams(start, end).mapExams()
            Mode.HYBRID, Mode.HEBE -> hebe.getExams(start, end).mapExams()
        }
    }

    suspend fun getGrades(semesterId: Int): Grades = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getGrades(semesterId).mapGrades()
            Mode.HYBRID, Mode.HEBE -> Triple(
                hebe.getGrades(semesterId),
                hebe.getGradesSummary(semesterId),
                hebe.getGradesAverage(semesterId),
            ).mapGrades()
        }
    }

    suspend fun getGradesSemesterStatistics(semesterId: Int): List<GradeStatisticsSemester> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesSemesterStatistics(semesterId).mapGradesSemesterStatistics()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getGradesPartialStatistics(semesterId: Int): List<GradeStatisticsSubject> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesPartialStatistics(semesterId).mapGradeStatistics()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getGradesPointsStatistics(semesterId: Int): List<GradePointsStatistics> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesPointsStatistics(semesterId).mapGradePointsStatistics()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getHomework(start: LocalDate, end: LocalDate): List<Homework> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getHomework(start, end).mapHomework()
            Mode.HYBRID, Mode.HEBE -> hebe.getHomework(studentId, start, end).mapHomework()
        }
    }

    suspend fun getNotes(): List<Note> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getNotes().mapNotes()
            Mode.HYBRID, Mode.HEBE -> hebe.getNotes(studentId).mapNotes()
        }
    }

    suspend fun getConferences(pupilId: Int = 0, startDate: LocalDate = LocalDate.of(1970, 1, 1)): List<Conference> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getConferences().mapConferences(registerTimeZone)
            Mode.HYBRID, Mode.HEBE -> hebe.getMeetings(pupilId, startDate).mapConferences(registerTimeZone)
        }
    }

    suspend fun getMenu(date: LocalDate): List<Menu> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMenu(date).mapMenu()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getRegisteredDevices(): List<Device> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getRegisteredDevices().mapDevices(registerTimeZone)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getToken(): Token = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getToken().mapToken()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun unregisterDevice(id: Int): Boolean = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.unregisterDevice(id)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getTeachers(periodId: Int): List<Teacher> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getTeachers().mapTeachers()
            Mode.HYBRID, Mode.HEBE -> hebe.getTeachers(periodId).mapTeachers()
        }
    }

    suspend fun getSchool(): School = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSchool().mapSchool()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getStudentInfo(): StudentInfo = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentInfo().mapStudent()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getStudentPhoto(): StudentPhoto = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentPhoto().mapPhoto()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getMailboxes(): List<Mailbox> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getMailboxes().mapMailboxes()
            Mode.HYBRID, Mode.HEBE -> hebe.getMailboxes().mapMailboxes()
        }
    }

    suspend fun getRecipients(mailboxKey: String): List<Recipient> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getRecipients(mailboxKey).mapRecipients()
            Mode.HYBRID, Mode.HEBE -> hebe.getRecipients(mailboxKey).mapRecipients()
        }
    }

    suspend fun getMessages(folder: Folder, mailboxKey: String? = null): List<Message> = withContext(Dispatchers.IO) {
        when (folder) {
            Folder.RECEIVED -> getReceivedMessages(mailboxKey)
            Folder.SENT -> getSentMessages(mailboxKey)
            Folder.TRASHED -> getDeletedMessages(mailboxKey)
        }
    }

    suspend fun getReceivedMessages(mailboxKey: String? = null): List<Message> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getReceivedMessages(mailboxKey).mapMessages(registerTimeZone, Folder.RECEIVED)
            Mode.HYBRID, Mode.HEBE -> hebe.getMessages(mailboxKey ?: "0", 1).mapMessages(registerTimeZone, Folder.RECEIVED)
        }
    }

    suspend fun getSentMessages(mailboxKey: String? = null): List<Message> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getSentMessages(mailboxKey).mapMessages(registerTimeZone, Folder.SENT)
            Mode.HYBRID, Mode.HEBE -> hebe.getMessages(mailboxKey ?: "0", 2).mapMessages(registerTimeZone, Folder.SENT)
        }
    }

    suspend fun getDeletedMessages(mailboxKey: String? = null): List<Message> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getDeletedMessages(mailboxKey).mapMessages(registerTimeZone, Folder.TRASHED)
            Mode.HYBRID, Mode.HEBE -> hebe.getMessages(mailboxKey ?: "0", 3).mapMessages(registerTimeZone, Folder.TRASHED)
        }
    }

    suspend fun getMessageReplayDetails(messageKey: String): MessageReplayDetails = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMessageReplayDetails(messageKey).mapScrapperMessage()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getMessageDetails(messageKey: String): MessageDetails = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMessageDetails(messageKey).mapScrapperMessage()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun markMessageRead(boxKey: String, messageKey: String) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.markMessageRead(messageKey)
            Mode.HYBRID, Mode.HEBE -> hebe.markMessageRead(hebe.pupilId, boxKey, messageKey)
        }
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<Recipient>, senderName: String, senderKey: String, partition: String) = withContext(
        Dispatchers.IO,
    ) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.sendMessage(subject, content, recipients.map { it.mailboxGlobalKey }, senderKey)
            Mode.HYBRID, Mode.HEBE -> hebe.sendMessage(
                subject,
                content,
                recipients.map {
                    MessageUser(
                        partition = partition,
                        globalKey = it.mailboxGlobalKey,
                        name = it.fullName,
                    )
                },
                MessageUser(
                    partition = partition,
                    globalKey = senderKey,
                    name = senderName,
                ),
            )
        }
    }

    suspend fun restoreMessages(messages: List<String>) = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.restoreMessages(messages)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun deleteMessages(messages: Pair<List<String>, List<String>>, pupilId: Int?, removeForever: Boolean = false) = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.deleteMessages(messages.first, removeForever)
            Mode.HYBRID, Mode.HEBE -> messages.first.forEachIndexed { index, it ->
                hebe.setMessageStatus(pupilId, messages.second[index], it, if (removeForever) 3 else 2)
            }
        }
    }

    suspend fun getTimetable(start: LocalDate, end: LocalDate): Timetable = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getTimetable(start, end).mapTimetableFull(registerTimeZone)
            Mode.HYBRID, Mode.HEBE -> hebe.getSchedule(studentId, start, end).mapTimetableFull(registerTimeZone)
        }
    }

    suspend fun getCompletedLessons(start: LocalDate, end: LocalDate, subjectId: Int = -1): List<CompletedLesson> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getCompletedLessons(start, end, subjectId).mapCompletedLessons()
            Mode.HYBRID, Mode.HEBE -> hebe.getCompletedLessons(studentId, start, end, subjectId).mapCompletedLessons()
        }
    }

    suspend fun getLuckyNumber(unitName: String = "", constituentId: Int): LuckyNumber? = withContext(Dispatchers.IO) {
        val numbers = getKidsLuckyNumbers(constituentId)

        // if lucky number unitName match unit name from student tile
        numbers.singleOrNull { number -> number.unitName == unitName }?.let {
            return@withContext it
        }

        // if there is only one lucky number and its doesn't match to any student
        if (numbers.size == 1) {
            return@withContext numbers.single()
        }

        // if there is more than one lucky number, return first (just like this was working before 0.16.0)
        if (numbers.size > 1) {
            return@withContext numbers.first()
        }

        // else
        null
    }

    suspend fun getDirectorInformation(): List<DirectorInformation> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getDirectorInformation().mapDirectorInformation()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getLastAnnouncements(): List<LastAnnouncement> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastAnnouncements().mapLastAnnouncements()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getSelfGovernments(): List<GovernmentUnit> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSelfGovernments().mapToUnits()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getStudentThreats(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentThreats()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getStudentsTrips(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentsTrips()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getLastGrades(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastGrades()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getFreeDays(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getFreeDays()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getKidsLuckyNumbers(constituentId: Int): List<LuckyNumber> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getKidsLuckyNumbers().mapLuckyNumbers()
            Mode.HYBRID, Mode.HEBE -> hebe.getLuckyNumber(studentId, constituentId).mapLuckyNumbers()
        }
    }

    suspend fun getKidsTimetable(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getKidsLessonPlan()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getLastHomework(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastHomework()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getLastExams(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastTests()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getLastStudentLessons(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastStudentLessons()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }
}
