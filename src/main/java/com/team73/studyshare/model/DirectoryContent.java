package com.team73.studyshare.model;

import com.team73.studyshare.model.data.Directory;
import com.team73.studyshare.model.data.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectoryContent {
    private List<Directory> subdirectories;
    private List<File> files;
}
