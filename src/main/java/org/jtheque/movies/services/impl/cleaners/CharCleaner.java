package org.jtheque.movies.services.impl.cleaners;

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

import org.jtheque.movies.persistence.od.able.Movie;
import org.jtheque.utils.StringUtils;

/**
 * @author Baptiste Wicht
 */
public final class CharCleaner implements NameCleaner {
    private final String character;

    /**
     * Construct a new CharCleaner with the specified character.
     *
     * @param character The character to clean.
     */
    public CharCleaner(String character) {
        super();

        this.character = character;
    }

    @Override
    public String getTitleKey() {
        return "movie.clean.cleaner.char";
    }

    @Override
    public Object[] getTitleReplaces() {
        return new Object[]{character};
    }

    @Override
    public String clearName(Movie movie, String name) {
        return StringUtils.delete(name, character);
    }
}