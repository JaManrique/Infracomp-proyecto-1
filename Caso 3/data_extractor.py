import os
from datetime import datetime

PERF_LOG = 'perfLogMiercoles.csv'
TESTS_FILE = 'testResults/results.txt'
RESULTS_FILE = 'results.res'

CPU = 'cpu'
MEMORY = 'memory'
NETWORK = 'network'

def extract_from_test(test_line):
  data = {}
  info = test_line.split(',')
  data['start'] = float(info[0])
  data['end'] = float(info[1])
  data['test_type'] = info[2]
  data['key_creation_time'] = info[3]
  data['avg_update_time'] = info[4]
  data['failed_requests'] = info[5]
  return data

def append_result(data):
  result = ''
  for att in data:
    result += data[att] + ','
  result = result[:-2] + '\n'
  with open(RESULTS_FILE, mode='a') as file:
    file.write(result)
  
def validInterval(perf_log_data, test_data):
  sample_date = datetime.strptime(perf_log_data[0], 'format').timestamp() #TODO comlete format with CSV
  return test_data['start'] < sample_date and sample_date < test_data['end']

def extract_from_perf_logs(data):
  cpu_avg = 0
  memory = 0
  network_bytes = 0
  count = 0
  with open(PERF_LOG, mode='r') as file:
    alreadyRead = False
    for line in file:
      info = line.split(',')
      if validInterval(info, data):
        alreadyRead = True
        #TODO Revisar si corresponde al csv del perfLog
        cpu_avg += int(info[0])
        memory += int(info[1])
        #info[2] = unused internet adapter
        network_bytes += int(info[3])
        count += 1
      #Past certain line, there should not be more lines between the test interval
      if alreadyRead and not validInterval(info, data):
        break
  data[CPU] = cpu_avg/count
  data[MEMORY] = memory/count
  data[NETWORK] = network_bytes/count
  #Method modifies data, so there is no need to return

def extract_data():
  with open(RESULTS_FILE) as file:
    for line in file:
      data = extract_from_test(line)
      extract_from_perf_logs(data)
      append_result(data)

extract_data()
