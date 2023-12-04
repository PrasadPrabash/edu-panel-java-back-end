package lk.ijse.dep11.edupanal.api;

import lk.ijse.dep11.edupanal.to.request.LecturerReqTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import javax.validation.Valid;
import java.sql.*;

@RestController
@RequestMapping("/api/v1/lecturers")
@CrossOrigin
public class LecturerHttpController {

    @Autowired
    private DataSource pool;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public void createNewLecturer(@ModelAttribute @Valid LecturerReqTO lecturer){
        try(Connection connection = pool.getConnection()) {
            connection.setAutoCommit(false);
            try {
                PreparedStatement stmInsertLecture = connection.prepareStatement("INSERT INTO lecturer" +
                        " ( name, designation, qualifications, linkedin) " +
                        "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);        // get generated id
                stmInsertLecture.setString(1, lecturer.getName());
                stmInsertLecture.setString(2, lecturer.getDesignation());
                stmInsertLecture.setString(3, lecturer.getQualifications());
                stmInsertLecture.setString(4, lecturer.getLinkedin());
                stmInsertLecture.executeUpdate();                                       // save the lecture in database
                ResultSet generatedKeys = stmInsertLecture.getGeneratedKeys();
                generatedKeys.next();
                int lectureId = generatedKeys.getInt(1);
                String picture = lectureId + "-" + lecturer.getName();                  // set picture path
                if (lecturer.getPicture() != null || !lecturer.getPicture().isEmpty()) {
                    PreparedStatement stmUpdateLecture = connection.prepareStatement("UPDATE lecturer SET picture = ? WHERE id = ?");
                    stmUpdateLecture.setString(1, picture);
                    stmUpdateLecture.setInt(2, lectureId);
                    stmUpdateLecture.executeUpdate();
                }
                final String table = lecturer.getType().equalsIgnoreCase("full-time") ? "full_time_rank" : "part_time_rank";

                Statement stm = connection.createStatement();
                ResultSet rst = stm.executeQuery("SELECT `rank` FROM "+ table +" ORDER BY `rank` DESC LIMIT 1");
                int rank;
                if(!rst.next()) rank = 1;
                else rank = rst.getInt("rank") + 1;
                PreparedStatement stmInsertRank = connection.prepareStatement("INSERT INTO "+ table +" (lecturer_id, `rank`) VALUES (?, ?)");
                stmInsertRank.setInt(1, lectureId);
                stmInsertRank.setInt(2, rank);
                stmInsertRank.executeUpdate();

                connection.commit();

            } catch (Throwable t) {
                connection.rollback();
                throw t;
            } finally {
                connection.setAutoCommit(true);
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/{lecturer-id}")
    public void updateLecturerDetails(){
        System.out.println("updateLecturerDetails()");
    }

    @DeleteMapping("/{lecturer-id}")
    public void deleteLecturer(){
        System.out.println("deleteLecturer()");

    }

    @GetMapping
    public void getAllLecturers(){
        System.out.println("getAllLecturers()");

    }
}
