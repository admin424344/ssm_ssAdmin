package com.imust.service.impl;

import com.github.pagehelper.PageHelper;
import com.imust.dao.IBedroomDao;
import com.imust.dao.IStuImageDao;
import com.imust.dao.IStudentDao;
import com.imust.domain.PageBeanUI;
import com.imust.domain.StuImage;
import com.imust.domain.Student;
import com.imust.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements IStudentService {

    @Autowired
    private IStudentDao studentDao;

    @Autowired
    private IBedroomDao bedroomDao;

    @Autowired
    private IStuImageDao stuImageDao;
    @Override
    public List<Student> findStudentList(PageBeanUI pageBeanUI) {
        PageHelper.startPage(pageBeanUI.getPageNumber(),pageBeanUI.getPageSize());
        return studentDao.findStudentList(pageBeanUI);
    }

    @Override
    public void saveStudent(PageBeanUI pageBeanUI) {
        //保存学生信息
        studentDao.saveStudent(pageBeanUI);
        //拿到最新插入学生的id
        int stuId = pageBeanUI.getStudent().getStuId();
        bedroomDao.update(pageBeanUI);
        //保存学生头像信息
        StuImage stuImage = pageBeanUI.getStudent().getStuImage();
        stuImage.getStudent().setStuId(stuId);
        stuImageDao.saveStuImage(stuImage);
    }

    @Override
    public Student findStudentById(PageBeanUI pageBeanUI) {
        return studentDao.findStudentById(pageBeanUI);
    }

    @Override
    public void updateStudent(PageBeanUI pageBeanUI) {
        //1.先把旧的信息恢复
        bedroomDao.updateOldBedRoom(pageBeanUI);

        //2.更新新的信息
        bedroomDao.update(pageBeanUI);

        //3.修改学生信息
        studentDao.updateStudent(pageBeanUI);
    }

    @Override
    public void deleteStudentById(int[] ids) {
        for (int stuId: ids) {
            //删除学生信息
            studentDao.deleteById(stuId);
            //删除头像信息
            stuImageDao.deleteStuImageById(stuId);
            //将bedRoom修改
            PageBeanUI pageBeanUI = new PageBeanUI();
            Student student = new Student();
            student.setStuId(stuId);
            pageBeanUI.setStudent(student);
            bedroomDao.updateOldBedRoom(pageBeanUI);
        }
    }
}
