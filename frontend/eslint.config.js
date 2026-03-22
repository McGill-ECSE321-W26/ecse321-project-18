//  @ts-check

import { tanstackConfig } from "@tanstack/eslint-config";
import tselint from "@typescript-eslint/eslint-plugin";
import tsparser from "@typescript-eslint/parser";
import prettierConfig from "eslint-config-prettier";
import prettierPlugin from "eslint-plugin-prettier";

export default [
  ...tanstackConfig,
  {
    languageOptions: {
      parser: tsparser,
      sourceType: "module",
    },
    plugins: {
      "@typescript-eslint": tselint,
      prettier: prettierPlugin,
    },
    rules: {
      ...prettierConfig.rules,
      "@typescript-eslint/no-unused-vars": "warn",
      "no-console": "warn",
      semi: ["error", "always"],
      quotes: ["error", "double"],
      "prettier/prettier": "error",
      "@typescript-eslint/array-type": "off",
      "@typescript-eslint/require-await": "off",
      "@typescript-eslint/only-throw-error": [
        "error",
        {
          allow: [
            {
              from: "package",
              package: "@tanstack/router-core",
              name: "Redirect",
            },
            {
              from: "package",
              package: "@tanstack/router-core",
              name: "NotFoundError",
            },
          ],
        },
      ],
      "pnpm/json-enforce-catalog": "off",
    },
  },
  {
    ignores: ["eslint.config.js", "prettier.config.js"],
  },
];
