package unq.api.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.Assert;
import unq.api.exceptions.InvalidTokenException;
import unq.api.model.*;
import unq.api.model.catalogs.SubjectOptions;
import unq.api.service.RepositoryService;
import unq.api.service.SurveyService;
import unq.utils.EnvConfiguration;
import unq.utils.SecurityTokenGenerator;
import unq.utils.SendEmailTLS;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/**
 * Created by mrivero on 28/9/16.
 */
public class SurveyServiceImpl implements SurveyService {

    private static final String NOT_YET = "not_yet";
    private static final String BAD_SCHEDULE = "bad_schedule";
    private static final String APPROVED = "approved";

    private static RepositoryService repositoryService = new RepositoryServiceImpl();
    private static Logger LOGGER = LoggerFactory.getLogger(SurveyServiceImpl.class);


    @Override
    public String saveStudent(Student student) {
        LOGGER.info(String.format("Saving student %s", student.getLegajo()));
        String token = SecurityTokenGenerator.getToken();
        student.setAuthToken(token);
        this.validateStudent(student);
        String saved = repositoryService.saveStudent(student);
        String url = getUrlNotification(token);
        SendEmailTLS.sendEmailSurveyNotification(student.getName(), student.getEmail(),url);
        LOGGER.info("Finish saving student and sending survey notification");
        return saved;
    }

    @Override
    public Student getStudentByID(String id) {
        LOGGER.info(String.format("Getting student %s", id));
        return repositoryService.getStudent(id);
    }

    @Override
    public String saveSurvey(Survey survey) {
        LOGGER.info(String.format("Saving survey for student %s", survey.getLegajo()));
        this.validateSurvey(survey);
        return repositoryService.saveSurvey(survey);
    }

    @Override
    public Survey getSurveyByStudent(String studentId, String year) {
        LOGGER.info(String.format("Getting survey by student %s and year %s", studentId, year));
        return repositoryService.getSurveyByStudent(studentId, year);
    }

    @Override
    public String saveSubject(Subject subject) {
        LOGGER.info(String.format("Starting saving subject %s", subject.getName()));
        return repositoryService.saveSubject(subject);
    }

    @Override
    public List<SubjectOptions> getAllSubjects(String year) {
        LOGGER.info(String.format("Start building subjects for year %s", year));
        List<SubjectOptions> options = new ArrayList<>();
        List<Subject> subjects = repositoryService.getSubjects(year);
        options.addAll(
                subjects.stream()
                        .map(subject -> new SubjectOptions(subject.getName(),
                                buildSelectionDates(subject.getDivisions()), subject.getGroup()))
                        .collect(Collectors.toList()));
        LOGGER.info("All subject finished successfully");
        return options;
    }

    @Override
    public List<ClassStatistics> getClassOccupation(String year) {
        LOGGER.info(String.format("Getting class occupation for year %s", year));

        List<ClassStatistics> classOccupations = null;
        try {
            classOccupations = this.calculateClassOccupationV2.get(year);
        } catch (Exception e) {
            LOGGER.error("Error trying to calculate class occupation", e);
            throw new RuntimeException(e);
        }

        LOGGER.info("Finish calculating class occupation");
        return classOccupations;
    }

    @Override
    public SurveyStudentData getSurveyStudentData(String year) {
        LOGGER.info(String.format("Getting survey data for year %s", year));
        Integer cantStudents = repositoryService.cantStudents();
        Integer cantSurveys = repositoryService.cantSurveys(year);
        SurveyStudentData surveyStudentData = new SurveyStudentData(cantStudents, cantSurveys, this.getPercentageCompletedSurveys(cantStudents, cantSurveys));
        LOGGER.info("Finishing survey data");
        return surveyStudentData;
    }

    @Override
    public SurveyModel getSurveyModel(String token, String year) {
        LOGGER.info(String.format("Getting survey model for token %s and year %s", token, year));
        Student studentByToken = repositoryService.getStudentByToken(token);
        Assert.notNull(studentByToken, "Student must not be null for token");
        Survey completedSurvey = repositoryService.getSurveyByStudent(studentByToken.getLegajo(), year);
        return new SurveyModel(studentByToken.getName(), studentByToken.getLegajo(), this.getAllSubjects(year),
                completedSurvey);
    }

    @Override
    public Set<String> getAllYears(){
        LOGGER.info("Getting all years");
        List<Subject> allSubjects = repositoryService.getAllSubjects();
        Set<String> years = new HashSet<String>();


        allSubjects.stream().forEach(subject ->
                years.add(subject.getSchoolYear()));

        LOGGER.info("Finish getting all active year");
        return years;
    }

    private void validateStudent(Student student) {
        LOGGER.info(String.format("Validating if %s already exist", student.getLegajo()));

        Student savedStudent = repositoryService.getStudent(student.getLegajo());
        Assert.isTrue(savedStudent==null, String.format("Student %s already exist", student.getLegajo()));
    }

    private String getUrlNotification(String token) {
        String domain = EnvConfiguration.configuration.getString("frontendDomain");
        return domain+token;
    }

    //TODO habria que borrar?
    private List<ClassOccupation> buildClassOccupations(String year) {
        List<ClassOccupation> classOccupations = new ArrayList<>();
        List<Survey> surveys = repositoryService.getSurveys(year);
        List<Subject> subjects = repositoryService.getSubjects(year);
        List<SelectedSubject> totalSelectedSubjects = new ArrayList<>();

        for(Survey survey: surveys){
            // get chosen subjects
            Stream<SelectedSubject> selectedSubjects = survey.getSelectedSubjects().stream().filter(
                    this::isSelected);
            totalSelectedSubjects.addAll(selectedSubjects.collect(Collectors.toList()));
        }

        Map<String, List<SelectedSubject>> seleccionBySubject = totalSelectedSubjects.stream().collect(
                groupingBy(SelectedSubject::getSubject));


        seleccionBySubject.forEach((subjectName, value) -> {
            Map<String, Long> countComision = value.stream().collect(
                    groupingBy(SelectedSubject::getStatus, counting()));

            countComision.forEach((comision, count) -> {
                List<Subject> subjectStream = subjects.stream().filter(subject -> subject.getName().equals(subjectName)).collect(Collectors.toList());
                Stream<Division> divisions = subjectStream.get(0).getDivisions().stream().filter(
                        division -> division.getComision().name().equals(comision.split(":")[0]));
                divisions.forEach(division -> {
                    long percentage = count * 100L / division.getQuota();

                    classOccupations.add(new ClassOccupation(subjectName, comision,count, percentage));
                });
            });

        });
        return classOccupations;
    }



    private List<ClassStatistics> buildClassOccupationsV2(String year) {
        final List<Survey> surveys = repositoryService.getSurveys(year);
        final List<ClassStatistics> response = Lists.newArrayList();

        surveys.stream()
                .map(Survey::getSelectedSubjects)
                .flatMap(Collection::stream)
                .collect(groupingBy(SelectedSubject::getSubject))
                .forEach((subjectName, responses) -> {

                    //Collect commission count and quota by subject
                    Map<String, Long> commissionCount = responses.stream().collect(groupingBy(this::keyMapper, counting()));
                    Map<Comision, Long> quotaBySubject = this.getQuotaBySubject(subjectName, year);

                    ClassStatistics.Builder builder = new ClassStatistics.Builder().subject(subjectName);

                    //Commission Data
                    builder.c1(new CommissionOccupation(quotaBySubject.get(Comision.C1), commissionCount.get(Comision.C1.name())));
                    builder.c2(new CommissionOccupation(quotaBySubject.get(Comision.C2), commissionCount.get(Comision.C2.name())));
                    builder.c3(new CommissionOccupation(quotaBySubject.get(Comision.C3), commissionCount.get(Comision.C3.name())));
                    builder.c4(new CommissionOccupation(quotaBySubject.get(Comision.C4), commissionCount.get(Comision.C4.name())));

                    //Custom values
                    builder.notYet(commissionCount.get(NOT_YET));
                    builder.approved(commissionCount.get(APPROVED));
                    builder.badSchedule(commissionCount.get(BAD_SCHEDULE));

                    response.add(builder.build());
                });
        return response;
    }

    private String keyMapper(SelectedSubject selectedSubject) {
        return selectedSubject.getStatus().split(":")[0];
    }

    /**
     * Returns a map with the quota by commission for that subject
     */
    private Map<Comision, Long> getQuotaBySubject(String subjectName, String year) {
        return repositoryService.getSubjects(year).stream()
                .filter(subject -> subject.getName().equalsIgnoreCase(subjectName))
                .map(Subject::getDivisions)
                .flatMap(Collection::stream)
                .collect(toMap(Division::getComision, Division::getQuota));
    }

    private LoadingCache<String, List<ClassStatistics>> calculateClassOccupationV2 = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(15L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, List<ClassStatistics>>() {
                        public List<ClassStatistics> load(String year) throws RuntimeException {
                            LOGGER.info("Missing key, calculating class occupation");
                            return buildClassOccupationsV2(year);
                        }
                    });

    //TODO habria que borrar?
    private LoadingCache<String, List<ClassOccupation>> calculateClassOccupation = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(15L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, List<ClassOccupation>>() {
                        public List<ClassOccupation> load(String year) throws RuntimeException {
                            LOGGER.info("Missing key, calculating class occupation");
                            return buildClassOccupations(year);
                        }
                    });


    private double getPercentageCompletedSurveys(Integer cantStudents, Integer cantSurvey){
        LOGGER.info("Getting percentage completed survey");

        int percentage = (cantSurvey * 100) / cantStudents;

        LOGGER.info("Finishing percentage completed survey");

        return percentage;
    }

    private boolean isSelected(SelectedSubject selectedSubject) {
        return !selectedSubject.getStatus().toLowerCase().equals(SubjectStatus.APPROVED.name().toLowerCase()) &&
                !selectedSubject.getStatus().toLowerCase().equals(SubjectStatus.BAD_SCHEDULE.name().toLowerCase()) &&
                !selectedSubject.getStatus().toLowerCase().equals(SubjectStatus.NOT_YET.name().toLowerCase());
    }

    private List<String> buildSelectionDates(List<Division> divisions) {
        return divisions.stream().map(subjectElection ->
                subjectElection.getComision()+": " + String.join(", ", subjectElection.getWeekdays())).collect(Collectors.toList());
    }

    private boolean completedSurvey(String id, List<Survey> surveys){
        Stream<Survey> studentSurvey = surveys.stream().filter(survey -> survey.getLegajo().equals(id));
        return (studentSurvey.count()>0);
    }

    private void validateSurvey(Survey survey) {
        LOGGER.info(String.format("Starting validation for user %s", survey.getStudentName()));
        Student studentByToken = repositoryService.getStudentByToken(survey.getToken());
        if (null != studentByToken) {
            LOGGER.info("Token student validation ok");
            return;
        }
        throw new InvalidTokenException("Invalid token, user now exist for that token "+survey.getToken());
    }

    @Override
    public String getLastActiveYear(){
        LOGGER.info("Starting calculating active year");
        Subject oldest = null;
        try {
            oldest = this.calculateLastActiveYear.get("lastActiveYear");
        } catch (ExecutionException e) {
            LOGGER.error("Error trying to get las active year", e);
            throw new RuntimeException(e);
        }

        LOGGER.info(String.format("Finish calculating last active year %s", oldest.getSchoolYear()));
        return oldest.getSchoolYear();

    }

    private LoadingCache<String, Subject> calculateLastActiveYear = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(30L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Subject>() {
                        public Subject load(String year) throws RuntimeException {
                            LOGGER.info("Missing key, calculating last active year");
                            return calculateLastActiveYear();
                        }
                    });


    private Subject calculateLastActiveYear() {
        List<Subject> allSubjects = repositoryService.getAllSubjects();


        final Comparator<Subject> comp = (p1, p2) -> Integer.compare( Integer.valueOf(p1.getSchoolYear()), Integer.valueOf(p2.getSchoolYear()));
        return allSubjects.stream()
                .max(comp)
                .get();
    }
}
