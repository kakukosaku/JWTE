package com.github.kakukosaku.exception;

/**
 * Description
 *
 * @author kaku
 * @date 2020-01-14
 */
public class CustomException extends Exception {

    CustomException(String msg) {
        super(msg);
    }

}


final class ApiUse {

    private void ageCheck(int age) throws CustomException {
        if (age > 18) {
            throw new CustomException("You are so old!");
        }
    }

    public static void main(String[] args) {
        ApiUse u = new ApiUse();
        try {
            u.ageCheck(19);
        } catch (CustomException e) {
            e.printStackTrace();
        }
    }

}

