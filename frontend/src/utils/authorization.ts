import { AccountType } from "#/types/api";

const FALLBACK_REDIRECT = "/";

export const redirectForAccountType = (
  accountType: AccountType | undefined,
) => {
  // determine where to redirect based on logged in user's account type
  // if attempting to access a page they do not have permissions for
  if (accountType) {
    if (
      accountType === AccountType.CUSTOMER ||
      accountType === AccountType.EMPLOYEE
    ) {
      return "/products";
    } else if (accountType === AccountType.OWNER) {
      return "/admin";
    }
  }

  return FALLBACK_REDIRECT;
};
