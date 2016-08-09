from jinja2 import Template, Environment, FileSystemLoader
import sys
import os
from configparser import ConfigParser

PROPERTY_FILE='local_conf_params.properties'
JINJA2_EXTENSION='j2'
RELATIVE_POS='../'
PATH = os.path.dirname(os.path.abspath(__file__))
TEMPLATE_ENVIRONMENT = Environment(
    autoescape=False,
    loader=FileSystemLoader(os.path.join(PATH, '../')),
    trim_blocks=False)

def render_template(template_filename, context):
    return TEMPLATE_ENVIRONMENT.get_template(template_filename).render(context)

def create_template_file(fname):

    config = parse_config()

    with open('../'+fname, 'w') as f:
        prop_file = render_template(fname+'.'+JINJA2_EXTENSION, config)
        f.write(prop_file)

def parse_config():

    config = {}

    with open(PROPERTY_FILE, "rt") as f:
        for line in f:
            l = line.strip()
            if l and not l.startswith('#'):
                key_value = l.split('=')
                config[key_value[0].strip()] = key_value[1].strip('" \t')

    return config

def main():
    for file in os.listdir("../"):
        if file.endswith("."+JINJA2_EXTENSION):
            filename, file_extension = os.path.splitext(file)
            create_template_file(filename)

    for file in os.listdir("../templates"):
        if file.endswith("."+JINJA2_EXTENSION):
            filename, file_extension = os.path.splitext(file)
            create_template_file("templates/"+filename)

########################################

if __name__ == "__main__":
    main()
