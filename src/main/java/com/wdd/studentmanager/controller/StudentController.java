package com.wdd.studentmanager.controller;

import com.csvreader.CsvReader;
import com.wdd.studentmanager.domain.Student;
import com.wdd.studentmanager.domain.Teacher;
import com.wdd.studentmanager.service.StudentService;
import com.wdd.studentmanager.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/system/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 跳转学生列表页面
     * @return
     */
    @GetMapping("/student_list")
    public String studentList(){
        return "/system/student/studentList";
    }

    /**
     * 异步加载学生列表
     * @param page
     * @param rows
     * @param studentId
     * @param from
     * @param session
     * @return
     */
    @RequestMapping("/getStudentList")
    @ResponseBody
    public Object getStudentList(@RequestParam(value = "page", defaultValue = "1")Integer page,
                                 @RequestParam(value = "rows", defaultValue = "100")Integer rows,
                                 String studentId,
                                 String from, HttpSession session){
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("pageno",page);
        paramMap.put("pagesize",rows);
        if(!StringUtils.isEmpty(studentId))  paramMap.put("studentId",studentId);

        //判断是老师:只查询自己可以看到的数据
        Teacher teacher = (Teacher) session.getAttribute(Const.TEACHER);
        if(!StringUtils.isEmpty(teacher)){
            paramMap.put("teacherId", teacher.getId());
        }

        PageBean<Student> pageBean = studentService.queryPage(paramMap);
        if(!StringUtils.isEmpty(from) && from.equals("combox")){
            return pageBean.getDatas();
        }else{
            Map<String,Object> result = new HashMap();
            result.put("total",pageBean.getTotalsize());
            result.put("rows", pageBean.getDatas());
            return result;
        }
    }

    /**
     * 删除学生
     * @param data
     * @return
     */
    @PostMapping("/deleteStudent")
    @ResponseBody
    public AjaxResult deleteStudent(Data data){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            List<Integer> ids = data.getIds();
//            Iterator<String> iterator = ids.iterator();
//
//            File fileDir = UploadUtil.getImgDirFile();
//            for(String id : ids){
//                Student byId = studentService.findById(id);
//                if(!byId.getPhoto().isEmpty()){
//                    File file = new File(fileDir.getAbsolutePath() + File.separator + byId.getPhoto());
//                    if(file != null){
//                        file.delete();
//                    }
//                }
//            }
            int count = studentService.deleteStudent(ids);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("全部删除成功");

            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("删除失败");
            }

        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("删除失败");
        }
        return ajaxResult;
    }


    /**
     * 添加学生
     * @param session
     * @param student
     * @return
     * @throws IOException
     */
    @RequestMapping("/addStudent")
    @ResponseBody
    public AjaxResult addStudent(@RequestParam("file") MultipartFile[] files, Student student, HttpSession session) throws IOException {
        AjaxResult ajaxResult = new AjaxResult();

        // 因为图片是必填项目，所以判断是否为空
        String fileName = files[0].getOriginalFilename();
        assert fileName != null;
        if (fileName.equals("")) {
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("信息截图不能为空！");
            return ajaxResult;
        }

        // 只支持PNG&JPG
        String exName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        if (!(exName.equals(".jpg")|| exName.equals("jpeg") || exName.equals(".png"))) {
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("信息截图格式只支持PNG或者JPG");
            return ajaxResult;
        }

        // 截图文件大小只支行1M

        // 存放上传图片的文件夹
        File fileDir = UploadUtil.getImgDirFile();
        for(MultipartFile fileImg : files){

            // 拿到文件名
            String extName = fileImg.getOriginalFilename().substring(fileImg.getOriginalFilename().lastIndexOf("."));
            String uuidName = UUID.randomUUID().toString();

            try {
                // 构建真实的文件路径
                File newFile = new File(fileDir.getAbsolutePath() + File.separator +uuidName+ extName);
                // 上传图片到 -》 “绝对路径”
                fileImg.transferTo(newFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
            String host = "";
            student.setPhoto(uuidName+extName);
        }
        //保存学生信息到数据库
        try{
            if (student.getPayDate().contains("/")) {
                // 格式化时间信息。
                String dateStr = student.getPayDate().split(" ")[0];
                String timeStr = student.getPayDate().split(" ")[1];
                String month = dateStr.split("/")[0];
                String day = dateStr.split("/")[1];
                String year = dateStr.split("/")[2];
                student.setPayDate(year + "-" + month + "-" + day + " " + timeStr);
            }else if (student.getPayDate().equals("")){
                student.setPayDate(null);
            }

            // 如果是教师添加学员的话，设置的teachId为当前登录账号的teacherId
            Teacher teacher = (Teacher) session.getAttribute(Const.TEACHER);
            if(!StringUtils.isEmpty(teacher)){
                student.setTeacherId(teacher.getId()+"");
            }
            // TODO 判断数据库中是否存在数据，如果已经存在，则不能添加，返回错误提示。
            Student result = studentService.findById(student.getStudentId());
            if (result != null) {
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("数据已存在，保存失败");
            } else {
                int count = studentService.addStudent(student);
                if(count > 0){
                    ajaxResult.setSuccess(true);
                    ajaxResult.setMessage("保存成功");
                }else{
                    ajaxResult.setSuccess(false);
                    ajaxResult.setMessage("保存失败");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("保存失败");
        }

        ajaxResult.setSuccess(true);
        return ajaxResult;
    }

    /**
     * 修改学生信息
     * @param student
     * @return
     */
    @PostMapping("/editStudent")
    @ResponseBody
    public AjaxResult editStudent(Student student){
        // 在有支付日期的情况下&&日期格式不符合要求时进行转换。
        if (student.getPayDate().contains("/")) {
            String dateStr = student.getPayDate().split(" ")[0];
            String timeStr = student.getPayDate().split(" ")[1];
            String month = dateStr.split("/")[0];
            String day = dateStr.split("/")[1];
            String year = dateStr.split("/")[2];
            student.setPayDate(year + "-" + month + "-" + day + " " + timeStr);
        }else {
            // 否则将支付日期设置null
            student.setPayDate(null);
        }

        AjaxResult ajaxResult = new AjaxResult();
        try{
            int count = studentService.editStudent(student);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("修改成功");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("修改失败");
            }
        }catch(Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("修改失败");
        }
        return ajaxResult;
    }

    @RequestMapping(value="/onlineupload", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,String> onlineupload(@RequestParam("onlinefilename") MultipartFile file,
                                           HttpServletRequest request) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStr = df.format(new Date());
        Map<String,String> map=new HashMap<>();
        String[] fileNameArray = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        String fileName = fileNameArray[0] + timeStr + "." + fileNameArray[1];
        String filePath="./datafiles/";
        try {
            uploadFile(file.getBytes(), filePath, fileName);
            boolean isSecess = readCSV(filePath + fileName);
            if (isSecess) {
                map.put("success","true");
                return map;
            }else {
                map.put("error","false");
                return map;
            }
        } catch (Exception e) {
            // TODO: handle exception
            map.put("error","false");
            return map;
        }
    }

    // 上传文件实现
    public void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }

    // 读取csv文件中的数据
    /**
     * @return Boolean
     * @Description 读取CSV文件的内容（不含表头）
     * @Param filePath 文件存储路径
     **/
    public Boolean readCSV(String filePath) {
        try {
            File file =new File(filePath);
            Student student = new Student();
            InputStreamReader isr=new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            CsvReader csvReader = new CsvReader(isr);
            int count = 0;
            while (csvReader.readRecord()) {
                if (count == 0) {
                    count++;
                    continue;
                }
                String[] item = csvReader.getRawRecord().split(",");
                student.setStudentId(item[2]);
                student.setUsername(item[3]);
                student.setBindPhone(item[4]);
                student.setPayPhone(item[5]);
                student.setName(item[6]);
                student.setAddress(item[7]);
                student.setPay(item[8]); // Float.parseFloat(item[8])
                student.setMark(item[9]);
                student.setTeacherId(item[10]);
                int resCount = studentService.addStudent(student);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
