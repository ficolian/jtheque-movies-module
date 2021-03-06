package org.jtheque.movies.services.impl.parsers;

import java.io.File;

/*
 * Copyright JTheque (Baptiste Wicht)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A file parser to extract the category from the parent folder.
 *
 * @author Baptiste Wicht
 */
public final class FolderCategoryParser extends AbstractSimpleCategoryParser {
    private static final Object[] REPLACES = new Object[0];

    @Override
    public String getTitleKey() {
        return "movie.auto.parser.folder";
    }

    @Override
    public Object[] getTitleReplaces() {
        return REPLACES;
    }

    @Override
    public void parseFilePath(File file) {
        if (file.isFile()) {
            String name = file.getParentFile().getName();

            addCategory(name);
        }
    }

    @Override
    public String clearFileName(String fileName) {
        return fileName;
    }
}