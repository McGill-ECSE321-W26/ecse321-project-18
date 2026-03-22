import { AccountType } from "#/types/api";

const FALLBACK_REDIRECT = "/";

export const redirectForAccountType = (
  accountType: AccountType | undefined,
) => {
  // determine where to redirect based on logged in user's account type
  // if attempting to access a page they do not have permissions for
  if (accountType) {
    if (
      accountType === AccountType.Customer ||
      accountType === AccountType.Employee
    ) {
      return "/products";
    } else if (accountType === AccountType.Owner) {
      return "/admin";
    }
  }

  return FALLBACK_REDIRECT;
};
