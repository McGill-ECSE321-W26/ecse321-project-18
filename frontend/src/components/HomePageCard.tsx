import type { IconType } from "react-icons";

export interface HomePageCardProps {
  title: string;
  description: string;
  logo?: IconType;
}

export default function HomePageCard(props: HomePageCardProps) {
  return (
    <div className="w-2xl p-10 m-5 rounded-2xl bg-gray-200">
      {props.logo && (
        <div className="flex justify-center pb-5">
          <props.logo size={50} />
        </div>
      )}
      <p className="font-bold text-3xl pb-5 flex justify-center">
        {props.title}
      </p>
      <p className="font-normal text-2xl">{props.description}</p>
    </div>
  );
}
