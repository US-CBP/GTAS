import React from "react";
import Title from "../../../components/title/Title";
import { Link } from "@reach/router";

const About = () => {
  return (
    <div className="container">
      <div className="column content">
        <Title title="About"></Title>
        <h1>Hello World</h1>
        <p>
          Lorem ipsum
          <sup>
            <Link to="../neo4j">[1]</Link>
          </sup>{" "}
          dolor sit amet, consectetur adipiscing elit. Nulla accumsan, metus ultrices
          eleifend gravida, nulla nunc varius lectus, nec rutrum justo nibh eu lectus. Ut
          vulputate semper dui. Fusce erat odio, sollicitudin vel erat vel, interdum
          mattis neque. Sub<sub>script</sub> works as well!
        </p>
        <h2>Second level</h2>
        <p>
          Curabitur accumsan turpis pharetra <strong>augue tincidunt</strong> blandit.
          Quisque condimentum maximus mi, sit amet commodo arcu rutrum id. Proin pretium
          urna vel cursus venenatis. Suspendisse potenti. Etiam mattis sem rhoncus lacus
          dapibus facilisis. Donec at dignissim dui. Ut et neque nisl.
        </p>
        <ul>
          <li>In fermentum leo eu lectus mollis, quis dictum mi aliquet.</li>
          <li>Morbi eu nulla lobortis, lobortis est in, fringilla felis.</li>
          <li>Aliquam nec felis in sapien venenatis viverra fermentum nec lectus.</li>
          <li>Ut non enim metus.</li>
        </ul>
        <h3>Third level</h3>
        <p>
          Quisque ante lacus, malesuada ac auctor vitae, congue non ante. Phasellus lacus
          ex, semper ac tortor nec, fringilla condimentum orci. Fusce eu rutrum tellus.
        </p>
        <ol>
          <li>Donec blandit a lorem id convallis.</li>
          <li>Cras gravida arcu at diam gravida gravida.</li>
          <li>Integer in volutpat libero.</li>
          <li>Donec a diam tellus.</li>
          <li>Aenean nec tortor orci.</li>
          <li>Quisque aliquam cursus urna, non bibendum massa viverra eget.</li>
          <li>Vivamus maximus ultricies pulvinar.</li>
        </ol>

        <blockquote>
          Ut venenatis, nisl scelerisque sollicitudin fermentum, quam libero hendrerit
          ipsum, ut blandit est tellus sit amet turpis.
        </blockquote>

        <p>
          Quisque at semper enim, eu hendrerit odio. Etiam auctor nisl et{" "}
          <em>justo sodales</em> elementum. Maecenas ultrices lacus quis neque
          consectetur, et lobortis nisi molestie.
        </p>
        <p>
          Sed sagittis enim ac tortor maximus rutrum. Nulla facilisi. Donec mattis
          vulputate risus in luctus. Maecenas vestibulum interdum commodo.
        </p>
        <dl>
          <dt>Web</dt>
          <dd>The part of the Internet that contains websites and web pages</dd>
          <dt>HTML</dt>
          <dd>A markup language for creating web pages</dd>
          <dt>CSS</dt>
          <dd>A technology to make HTML look better</dd>
        </dl>
        <p>
          Suspendisse egestas sapien non felis placerat elementum. Morbi tortor nisl,
          suscipit sed mi sit amet, mollis malesuada nulla. Nulla facilisi. Nullam ac erat
          ante.
        </p>
      </div>
    </div>
  );
};

export default About;
