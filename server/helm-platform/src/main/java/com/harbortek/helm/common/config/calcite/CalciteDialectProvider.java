/*
 * Copyright [2025] [Harbortek Corp.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package com.harbortek.helm.common.config.calcite;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.data.jdbc.core.dialect.JdbcMySqlDialect;
//import org.springframework.data.jdbc.repository.config.DialectResolver;
//import org.springframework.data.relational.core.dialect.Dialect;
//import org.springframework.data.relational.core.sql.IdentifierProcessing;
//import org.springframework.jdbc.core.ConnectionCallback;
//import org.springframework.jdbc.core.JdbcOperations;
//import org.springframework.util.StringUtils;
//
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.sql.SQLException;
//import java.util.Locale;
//import java.util.Optional;
//
//public class CalciteDialectProvider extends DialectResolver.DefaultDialectProvider {
//    private static final Log LOG = LogFactory.getLog(CalciteDialectProvider.class);
//
//    @Override
//    public Optional<Dialect> getDialect(JdbcOperations operations) {
//
//        return Optional.ofNullable(operations.execute((ConnectionCallback<Dialect>) CalciteDialectProvider::getDialect));
//    }
//
//    private static Dialect getDialect(Connection connection) throws SQLException {
//
//        DatabaseMetaData metaData = connection.getMetaData();
//        String name = metaData.getDatabaseProductName().toLowerCase(Locale.ENGLISH);
//
//        if (name.contains("calcite")) {
//            return new JdbcMySqlDialect(getIdentifierProcessing(metaData));
//        }
//
//        LOG.info(String.format("Couldn't determine Dialect for \"%s\"", name));
//        return null;
//
//    }
//
//    private static IdentifierProcessing getIdentifierProcessing(DatabaseMetaData metaData) throws SQLException {
//
//        // getIdentifierQuoteString() returns a space " " if identifier quoting is not
//        // supported.
//        String quoteString = metaData.getIdentifierQuoteString();
//        IdentifierProcessing.Quoting quoting = StringUtils.hasText(quoteString)
//                ? new IdentifierProcessing.Quoting(quoteString)
//                : IdentifierProcessing.Quoting.NONE;
//
//        IdentifierProcessing.LetterCasing letterCasing;
//        // IdentifierProcessing tries to mimic the behavior of unquoted identifiers for their quoted variants.
//        if (metaData.supportsMixedCaseIdentifiers()) {
//            letterCasing = IdentifierProcessing.LetterCasing.AS_IS;
//        } else if (metaData.storesUpperCaseIdentifiers()) {
//            letterCasing = IdentifierProcessing.LetterCasing.UPPER_CASE;
//        } else if (metaData.storesLowerCaseIdentifiers()) {
//            letterCasing = IdentifierProcessing.LetterCasing.LOWER_CASE;
//        } else { // this shouldn't happen since one of the previous cases should be true.
//            // But if it does happen, we go with the ANSI default.
//            letterCasing = IdentifierProcessing.LetterCasing.UPPER_CASE;
//        }
//
//        return IdentifierProcessing.create(quoting, letterCasing);
//    }
//}