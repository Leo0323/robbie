package com.contro;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dto.CommentDto;
import com.service.CommentService;
import com.vo.Comment;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {
    @Resource
    private CommentService commentService;

    @PostMapping("/createComm")
    public String createComm(@RequestBody Comment comment){
        return commentService.createComm(comment);
    }
    @GetMapping("/getParComm/{fileId}")
    public IPage<CommentDto> getParComm(@PathVariable("fileId") int fileId,@RequestParam("page") int page){
        return commentService.getParComm(fileId, page);
    }
    @GetMapping("/getSubComm/{pid}/{fileId}")
    public IPage<CommentDto> getSubComm(@PathVariable("pid") int pid,@PathVariable("fileId") int fileId,@RequestParam("page") int page){
        return commentService.getSubComm(pid,fileId,page);
    }
    @GetMapping("/deleteComm/{id}")
    public void deleteComm(@PathVariable("id") Long id){
        commentService.deleteComm(id);
    }

    @GetMapping ("/getCommCount/{fileId}")
    public Long getCommCount(@PathVariable("fileId") int fileId){
        return commentService.count(fileId);
    }
}
