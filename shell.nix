{
pkgs ? import <nixpkgs> { },
}:

pkgs.mkShell {
  buildInputs = with pkgs; [
    jdk21
    gradle
    python3
    docker-compose
  ];
}
