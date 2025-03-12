package pbs.sme.survey.helper;

public class FilterHelper {

    public static String checkInjection(String userInput) {
        String[] sqlCheckList = {
                "'",

                "\"",

                "\'",

                "--",

                ";--",

                ";",

                "/*",

                "*/",

                "@@",

                "char",

                "nchar",

                "varchar",

                "nvarchar",

                "alter",

                "begin",

                "cast",

                "create",

                "cursor",

                "declare",

                "delete",

                "drop",

                "end",

                "exec",

                "execute",

                "fetch",

                "insert",

                "kill",

                "select",

                "sys",

                "sysobjects",

                "syscolumns",

                "table",

                "update"

        };

        for (int i = 0; i <= sqlCheckList.length - 1; i++) {
            if (userInput.contains(sqlCheckList[i])) {
                return sqlCheckList[i];
            }
        }
        return null;
    }
}
