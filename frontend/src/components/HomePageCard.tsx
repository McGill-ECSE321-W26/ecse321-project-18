export default function HomePageCard(props) {
  return (
    <div className="w-2xl m-10">
      <p className="font-bold text-3xl pb-5">{props.title}</p>
      <p className="font-normal text-2xl">{props.description}</p>
    </div>
  );
}
