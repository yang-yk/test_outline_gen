package com.iscas.testoutline.service;

import com.iscas.testoutline.dto.DocumentResponseDTO;
import com.iscas.testoutline.dto.GenerateOutlineDTO;
import com.iscas.testoutline.dto.TestOutlineResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OutlineService {
    DocumentResponseDTO uploadRequirement(String email, MultipartFile file);
    List<DocumentResponseDTO> getRequirementList(String email);
    List<TestOutlineResponseDTO> getOutlineList(String email);
    TestOutlineResponseDTO generateOutline(String email, GenerateOutlineDTO generateDTO);
    void deleteDocument(String email, Long id, String type);
    byte[] downloadDocument(String email, Long id, String type);
    TestOutlineResponseDTO saveOutline(String email, Long id, String content);
} 